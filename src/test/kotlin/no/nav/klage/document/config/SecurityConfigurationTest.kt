package no.nav.klage.document.config

import jakarta.servlet.ServletException
import no.nav.klage.auth.test.AzureSecurityContractTest
import no.nav.klage.auth.test.MockAzure.Companion.NAV_IDENT_CLAIM
import no.nav.klage.document.api.CommentsController
import no.nav.klage.document.api.DocumentController
import no.nav.klage.document.service.CommentService
import no.nav.klage.document.service.DocumentService
import no.nav.klage.document.util.TokenUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(DocumentController::class, CommentsController::class)
@Import(SecurityConfiguration::class, TokenUtil::class)
class SecurityConfigurationTest : AzureSecurityContractTest() {
    @MockitoBean
    lateinit var documentService: DocumentService

    @MockitoBean
    lateinit var commentService: CommentService

    override fun protectedRequest() = delete("/documents/${UUID.randomUUID()}")

    @Test
    fun `passes NAVident from token to comment service`() {
        val documentId = UUID.randomUUID()
        val commentId = UUID.randomUUID()

        mockMvc
            .perform(
                delete("/documents/$documentId/comments/$commentId")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ${mockAzure.token(claims = mapOf(NAV_IDENT_CLAIM to "Z123456"))}"),
            ).andExpect(status().isOk)

        verify(commentService).deleteComment(commentId = commentId, loggedInIdent = "Z123456", behandlingTildeltIdent = null)
    }

    @Test
    fun `fails comment deletion when token has no NAVident`() {
        assertThrows<ServletException> {
            mockMvc.perform(
                delete("/documents/${UUID.randomUUID()}/comments/${UUID.randomUUID()}")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ${mockAzure.token()}"),
            )
        }

        verifyNoInteractions(commentService)
    }
}
