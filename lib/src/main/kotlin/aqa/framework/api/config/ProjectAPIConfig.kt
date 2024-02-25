package aqa.framework.api.config

import io.restassured.RestAssured
import io.restassured.builder.ResponseSpecBuilder
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import org.hamcrest.Matchers
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope


//@ComponentScan
//@Configuration
//@PropertySource("application-\${spring.profiles.active}.yml", factory = YamlPropertySource::class)
open class ProjectAPIConfig {
    @Value("\${restassured.response.time:10000}")
    private lateinit var timeout: String

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun restAssuredConfiguration() {
        RestAssured.filters(RequestLoggingFilter(), ResponseLoggingFilter())
        RestAssured.useRelaxedHTTPSValidation()
        RestAssured.responseSpecification =
            ResponseSpecBuilder().expectResponseTime(Matchers.lessThan(timeout.toLong())).build()
    }
}
