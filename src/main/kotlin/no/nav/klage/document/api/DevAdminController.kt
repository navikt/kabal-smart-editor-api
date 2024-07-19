package no.nav.klage.document.api

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.document.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.document.service.DocumentService
import no.nav.klage.document.util.getLogger
import no.nav.klage.document.util.getSecureLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.web.bind.annotation.*

@RestController
@ProtectedWithClaims(issuer = ISSUER_AAD)
@Tag(name = "kabal-smart-editor-api")
class DevAdminController(
    private val documentService: DocumentService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Unprotected
    @GetMapping("/internal/documents/invalid")
    fun logInvalidDocuments() {
        documentService.logDocumentsWithInvalidJson()
    }
}