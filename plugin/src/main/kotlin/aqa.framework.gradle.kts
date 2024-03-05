import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDateTime

val versionCatalog = rootProject.extensions
    .getByType(VersionCatalogsExtension::class.java)
    .named("libs")
val jvm = versionCatalog.findVersion("jvm").get().displayName

fun String.toLibPlugin(): String =
    versionCatalog.findPlugin(this).get().get().pluginId

fun String.toLibLibrary() =
    versionCatalog.findLibrary(this).get()

fun String.toLibBundle() =
    versionCatalog.findBundle(this).get()

plugins {
    kotlin("jvm")
}

allprojects {
    arrayOf(
        "kotlin".toLibPlugin(),
        "kotlin-serialization".toLibPlugin(),
        "allure".toLibPlugin(),
        "allure-aggregate-report".toLibPlugin(),
        "java-library",
        "ktlint".toLibPlugin(),
    ).forEach { apply(plugin = it) }

    repositories {
        mavenLocal()
        mavenCentral()
        try {
            maven {
                url = uri(providers.gradleProperty("ARTIFACTORY_REPO_URI").get())
                credentials {
                    username = providers.gradleProperty("ARTIFACTORY_REPO_USER").orNull
                    password = providers.gradleProperty("ARTIFACTORY_REPO_PASS").orNull
                }
            }
        } catch (e: Exception) {
            logger.warn("Unable to configure maven repository since ARTIFACTORY_REPO_* variables were not set")
        }
    }

    dependencies {
        arrayOf(
            kotlin("test"),
            "kotlin-lib".toLibLibrary(),
            "kotlin-serialization-json".toLibLibrary(),

            "totp".toLibLibrary(),
            "json-assert".toLibLibrary(),

            "kotest".toLibBundle(),
            "junit5".toLibBundle(),
            "spring-boot-test".toLibBundle(),
            "qase".toLibBundle(),
            "restassured".toLibBundle(),
            "allure-java-commons".toLibLibrary(),
        ).forEach { api(it) }
    }

    tasks {
        register<Exec>("gitSubmodulesUpdate") {
            group = "tools"
            description = "Update all git submodules from remote repo"
            commandLine("git submodule update --init --recursive --force --remote".split(" "))
        }

        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict", "-Xcontext-receivers")
                jvmTarget = jvm
            }
        }

        withType<JavaCompile> {
            javaCompiler.set(
                project.javaToolchains.compilerFor {
                    languageVersion.set(JavaLanguageVersion.of(jvm))
                }
            )
        }

        test {
            ignoreFailures = true
            doFirst {
                infix fun String.setProperty(key: String) {
                    logger.quiet("Set property ---> $key='$this'")
                    systemProperty(key, this)
                }

                arrayOf(
                    "QASE_API_TOKEN",
                    "QASE_ENABLE",
                    "QASE_PROJECT_CODE",
                    "QASE_RUN_ID",
                    "QASE_RUN_NAME",
                    "QASE_RUN_AUTOCOMPLETE",
                    "QASE_RUN_DESCRIPTION",
                    "TEST_ENV",
                ).forEach { key ->
                    (providers.systemProperty(key).orNull
                        ?: providers.gradleProperty(key).orNull)
                        ?.also { value -> value setProperty key }
                }

                "QASE_RUN_NAME".also {
                    if (!providers.systemProperty(it).isPresent || !providers.gradleProperty(it).isPresent) {
                        "Autotest run: ${LocalDateTime.now()}" setProperty it
                        "Test run automatically generated by [${providers.environmentVariable("USER").orNull}]" setProperty "QASE_RUN_DESCRIPTION"
                    }
                }
            }

            useJUnitPlatform {
                if (project.hasProperty("includeTags")) {
                    includeTags = setOf(project.property("includeTags").toString())
                }
            }

            reports {
//                html.required = true
                junitXml.required = true
            }

            testLogging {
                events(PASSED, SKIPPED, FAILED)
                exceptionFormat = TestExceptionFormat.FULL
                showCauses = true
                showExceptions = true
                showStackTraces = true
            }
        }
    }
}
