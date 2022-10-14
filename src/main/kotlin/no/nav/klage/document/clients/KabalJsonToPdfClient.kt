package no.nav.klage.document.clients

import brave.Tracer
import no.nav.klage.document.domain.PDFDocument
import no.nav.klage.document.exceptions.ValidationException
import no.nav.klage.document.util.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.client.toEntity

@Component
class KabalJsonToPdfClient(
    private val kabalJsonToPdfWebClient: WebClient,
    private val tracer: Tracer
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
            .header("Nav-Call-Id", tracer.currentSpan().context().traceIdString())
            .retrieve()
            .toEntity(ByteArray::class.java)
            .map {
                val filename = it.headers["filename"]?.first()
                PDFDocument(
                    filename = filename
                        ?: "somefilename",//throw RuntimeException("Could not get filename from headers"),
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
            .header("Nav-Call-Id", tracer.currentSpan().context().traceIdString())
            .retrieve()
            .onStatus(
                { status: HttpStatus -> status.isError },
                { errorResponse: ClientResponse ->
                    errorResponse.toEntity<String>().subscribe { entity: ResponseEntity<String> ->
                        logger.error("Feilet med å validere dokument. Feil: {}", entity.toString())
                        throw ValidationException(entity.toString())
                    }
                    errorResponse.createException()
                })

            .bodyToMono<Unit>()
            .block() ?: throw RuntimeException("kabal-json-to-pdf could not be reached")
    }
}