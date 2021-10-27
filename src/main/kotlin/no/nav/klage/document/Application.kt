package no.nav.klage.document

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer




@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
class Application

fun main() {
    runApplication<Application>()
}