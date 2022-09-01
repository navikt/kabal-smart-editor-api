package no.nav.klage.document.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.document.api.views.CommentInput
import no.nav.klage.document.api.views.CommentView
import no.nav.klage.document.api.views.DocumentView
import no.nav.klage.document.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.document.domain.Comment
import no.nav.klage.document.domain.Document
import no.nav.klage.document.service.CommentService
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
    private val commentService: CommentService,
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
        summary = "Create comment for a given document",
        description = "Create comment for a given document"
    )
    @PostMapping("/{documentId}/comments")
    fun createComment(
        @PathVariable("documentId") documentId: UUID,
        @RequestBody commentInput: CommentInput
    ): CommentView {
        log("createComment called with id $documentId")
        return mapCommentToView(
            commentService.createComment(
                documentId = documentId,
                text = commentInput.text,
                authorName = commentInput.author.name,
                authorIdent = commentInput.author.ident
            )
        )
    }

    @Operation(
        summary = "Get all comments for a given document",
        description = "Get all comments for a given document"
    )
    @GetMapping("/{documentId}/comments")
    fun getAllCommentsWithPossibleThreads(
        @PathVariable("documentId") documentId: UUID
    ): List<CommentView> {
        log("getAllCommentsWithPossibleThreads called with id $documentId")
        return commentService.getComments(documentId).map { mapCommentToView(it) }
    }

    @Operation(
        summary = "Reply to a given comment",
        description = "Reply to a given comment"
    )
    @PostMapping("/{documentId}/comments/{commentId}/replies")
    fun replyToComment(
        @PathVariable("documentId") documentId: UUID,
        @PathVariable("commentId") commentId: UUID,
        @RequestBody commentInput: CommentInput,
    ): CommentView {
        log("replyToComment called with id $documentId and commentId $commentId")
        return mapCommentToView(
            commentService.replyToComment(
                documentId = documentId,
                parentCommentId = commentId,
                text = commentInput.text,
                authorName = commentInput.author.name,
                authorIdent = commentInput.author.ident
            )
        )
    }

    @Operation(
        summary = "Get a given comment",
        description = "Get a given comment"
    )
    @GetMapping("/{documentId}/comments/{commentId}")
    fun getCommentWithPossibleThread(
        @PathVariable("documentId") documentId: UUID,
        @PathVariable("commentId") commentId: UUID
    ): CommentView {
        log("getCommentWithPossibleThread called with id $documentId and commentId $commentId")
        return mapCommentToView(commentService.getComment(commentId = commentId))
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

    private fun mapToDocumentView(document: Document): DocumentView =
        DocumentView(
            id = document.id,
            json = document.json,
            created = document.created,
            modified = document.modified
        )

    private fun mapCommentToView(comment: Comment): CommentView =
        CommentView(
            id = comment.id,
            text = comment.text,
            author = CommentView.Author(
                name = comment.authorName,
                ident = comment.authorIdent
            ),
            comments = comment.comments.map { mapCommentToView(it) },
            created = comment.created,
            modified = comment.modified
        )

    private fun log(message: String) {
        logger.debug(message)
        secureLogger.debug("{}. On-behalf-of: {}", message, getIdent())
    }

    fun getIdent(): String? =
        tokenValidationContextHolder.tokenValidationContext.getJwtToken(ISSUER_AAD)
            .jwtTokenClaims?.get("NAVident")?.toString()

}