package aqa.framework.api.base

import aqa.framework.api.config.ProjectAPIConfig
import io.kotest.core.spec.style.AnnotationSpec
import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [ProjectAPIConfig::class])
open class BaseTest : AnnotationSpec() {
    val log: Logger = LoggerFactory.getLogger(javaClass)
}
