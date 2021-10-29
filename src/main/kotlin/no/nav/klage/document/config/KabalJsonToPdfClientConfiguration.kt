package no.nav.klage.document.config

import no.nav.klage.document.util.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class KabalJsonToPdfClientConfiguration(
    private val webClientBuilder: WebClient.Builder
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    private var appURL = "http://kabal-json-to-pdf"

    @Bean
    fun kabalJsonToPdfWebClient(): WebClient {
        return webClientBuilder
            .baseUrl(appURL)
            .build()
    }
}