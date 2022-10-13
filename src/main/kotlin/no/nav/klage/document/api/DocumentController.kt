package no.nav.klage.document.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.document.api.views.DocumentView
import no.nav.klage.document.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.document.domain.Document
import no.nav.klage.document.service.DocumentService
import no.nav.klage.document.util.getLogger
import no.nav.klage.document.util.getSecureLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@ProtectedWithClaims(issuer = ISSUER_AAD)
@Tag(name = "kabal-smart-editor-api")
@RequestMapping("/documents")
class DocumentController(
    private val documentService: DocumentService,
    private val tokenValidationContextHolder: TokenValidationContextHolder
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Operation(
        summary = "Create document",
        description = "Create document"
    )
    @PostMapping("")
    fun createDocument(
        @RequestBody json: String
    ): DocumentView {
        log("createDocument")
        secureLogger.debug("createDocument: received json: {}", json)
        return mapToDocumentView(documentService.createDocument(json))
    }

    @Operation(
        summary = "Update document",
        description = "Update document"
    )
    @PutMapping("/{documentId}")
    fun updateDocument(
        @PathVariable("documentId") documentId: UUID,
        @RequestBody json: String
    ): DocumentView {
        log("updateDocument called with id $documentId")
        secureLogger.debug("updateDocument with id {}: received json: {}", documentId, json)
        return mapToDocumentView(documentService.updateDocument(documentId, json))
    }

    @Operation(
        summary = "Get document",
        description = "Get document"
    )
    @GetMapping("/{documentId}")
    fun getDocument(@PathVariable("documentId") documentId: UUID): DocumentView {
        log("getDocument called with id $documentId")
        return mapToDocumentView(documentService.getDocument(documentId))
    }

    @Operation(
        summary = "Delete document",
        description = "Delete document"
    )
    @DeleteMapping("/{documentId}")
    fun deleteDocument(@PathVariable("documentId") documentId: UUID) {
        log("deleteDocument called with id $documentId")
        documentService.deleteDocument(documentId)
    }

    @Operation(
        summary = "Generer PDF",
        description = "Generer PDF"
    )
    @ResponseBody
    @GetMapping("/{documentId}/pdf")
    fun getDocumentAsPDF(
        @PathVariable("documentId") documentId: UUID
    ): ResponseEntity<ByteArray> {
        log("getDocumentAsPDF with id : $documentId")

        val pdfDocument = documentService.getDocumentAsPDF(documentId)

        val responseHeaders = HttpHeaders()
        responseHeaders.contentType = MediaType.APPLICATION_PDF
        responseHeaders.add("Content-Disposition", "inline; filename=${pdfDocument.filename}.pdf")
        return ResponseEntity(
            pdfDocument.bytes,
            responseHeaders,
            HttpStatus.OK
        )
    }

    @Operation(
        summary = "Validerer dokument",
        description = "Validerer dokument"
    )
    @ResponseBody
    @GetMapping("/{documentId}/validate")
    fun validateDocument(
        @PathVariable("documentId") documentId: UUID
    ) {
        log("${::validateDocument.name} with id : $documentId")
        documentService.validateDocument(documentId)
    }

    private fun mapToDocumentView(document: Document): DocumentView =
        DocumentView(
            id = document.id,
            json = document.json,
            created = document.created,
            modified = document.modified
        )

    private fun log(message: String) {
        logger.debug(message)
        secureLogger.debug("{}. On-behalf-of: {}", message, getIdent())
    }

    fun getIdent(): String? =
        tokenValidationContextHolder.tokenValidationContext.getJwtToken(ISSUER_AAD)
            .jwtTokenClaims?.get("NAVident")?.toString()

}