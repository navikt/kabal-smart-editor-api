package no.nav.klage.document.config

import no.nav.klage.document.api.DocumentController
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun apiInternal(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .packagesToScan(DocumentController::class.java.packageName)
            .group("default")
            .pathsToMatch("/**")
            .build()
    }

}
