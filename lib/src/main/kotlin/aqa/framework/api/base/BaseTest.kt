package aqa.framework.api.base

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import aqa.framework.api.config.ProjectConfig
import io.kotest.core.spec.style.AnnotationSpec

@SpringBootTest(classes = [ProjectConfig::class])
open class BaseTest : AnnotationSpec() {
    val log: Logger = LoggerFactory.getLogger(javaClass)
}
