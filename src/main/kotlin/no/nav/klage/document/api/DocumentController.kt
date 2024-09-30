package no.nav.klage.document.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.document.api.views.DocumentUpdateInput
import no.nav.klage.document.api.views.DocumentVersionView
import no.nav.klage.document.api.views.DocumentView
import no.nav.klage.document.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.document.service.DocumentService
import no.nav.klage.document.util.TokenUtil
import no.nav.klage.document.util.getLogger
import no.nav.klage.document.util.getSecureLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@ProtectedWithClaims(issuer = ISSUER_AAD)
@Tag(name = "kabal-smart-editor-api")
@RequestMapping("/documents")
class DocumentController(
    private val documentService: DocumentService,
    private val tokenUtil: TokenUtil,
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
        @RequestBody input: DocumentUpdateInput,
    ): DocumentView {
        log("createDocument")
        secureLogger.debug("createDocument: received json: {}", input.json)
        return documentService.createDocument(json = input.json, data = input.data)
    }

    @Operation(
        summary = "Update document",
        description = "Update document"
    )
    @PutMapping("/{documentId}")
    fun updateDocument(
        @PathVariable("documentId") documentId: UUID,
        @RequestBody input: DocumentUpdateInput,
    ): DocumentView {
        log("updateDocument called with id $documentId")
        secureLogger.debug(
            "updateDocument with id {}: current FE version: {} received json: {}",
            documentId,
            input.currentVersion,
            input.json
        )

        return try {
            documentService.updateDocument(
                documentId = documentId,
                json = input.json,
                data = input.data,
                currentVersion = input.currentVersion,
            )

        } catch (e: Exception) {
            logger.warn("Failed to update document $documentId. Trying one more time.", e)
            documentService.updateDocument(
                documentId = documentId,
                json = input.json,
                data = input.data,
                currentVersion = input.currentVersion,
            )
        }
    }

    @Operation(
        summary = "Get document",
        description = "Get document"
    )
    @GetMapping("/{documentId}", "/{documentId}/versions/{version}")
    fun getDocument(
        @PathVariable("documentId") documentId: UUID,
        @PathVariable("version", required = false) version: Int?,
    ): DocumentView {
        log("getDocument called with id $documentId and version $version")
        return documentService.getDocument(documentId = documentId, version = version)
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
        summary = "Get document versions",
        description = "Get document versions"
    )
    @GetMapping("/{documentId}/versions")
    fun getDocumentVersions(@PathVariable("documentId") documentId: UUID): List<DocumentVersionView> {
        log("getDocumentVersions called with id $documentId")
        return documentService.getDocumentVersions(documentId = documentId)
    }

    private fun log(message: String) {
        logger.debug(message)
        secureLogger.debug("{}. On-behalf-of: {}", message, tokenUtil.getIdentNullable())
    }

}