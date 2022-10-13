package no.nav.klage.document.clients

import no.nav.klage.document.domain.PDFDocument
import no.nav.klage.document.exceptions.ValidationException
import no.nav.klage.document.util.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component
class KabalJsonToPdfClient(
    private val kabalJsonToPdfWebClient: WebClient
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun getPDFDocument(json: String): PDFDocument {
        return kabalJsonToPdfWebClient.post()
            .uri { it.path("/topdf").build() }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(json)
            .retrieve()
            .toEntity(ByteArray::class.java)
            .map {
                val filename = it.headers["filename"]?.first()
                PDFDocument(
                    filename = filename ?: "somefilename",//throw RuntimeException("Could not get filename from headers"),
                    bytes = it.body ?: throw RuntimeException("Could not get PDF data")
                )
            }
            .block() ?: throw RuntimeException("PDF could not be created")
    }

    fun validateDocument(json: String) {
        return kabalJsonToPdfWebClient.post()
            .uri { it.path("/validate").build() }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(json)
            .retrieve()
            .onStatus(HttpStatus::isError) { response ->
                logger.debug("error when validating: $response")
                response.bodyToMono<String>().map { ValidationException(it) }
            }
            .bodyToMono<Unit>()
            .block() ?: throw RuntimeException("kabal-json-to-pdf could not be reached")
    }
}