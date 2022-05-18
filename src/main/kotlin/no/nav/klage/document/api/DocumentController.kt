package no.nav.klage.document.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import no.nav.klage.document.api.views.CommentInput
import no.nav.klage.document.api.views.CommentView
import no.nav.klage.document.api.views.DocumentInput
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
@Api(tags = ["kabal-smart-editor-api"])
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

    @ApiOperation(
        value = "Create document",
        notes = "Create document"
    )
    @PostMapping("")
    fun createDocument(
        @RequestBody input: DocumentInput
    ): DocumentView {
        log("createDocument")
        secureLogger.debug("createDocument: received input: {}", input)
        return mapToDocumentView(documentService.createDocument(json = input.json, templateId = input.templateId))
    }

    @ApiOperation(
        value = "Update document",
        notes = "Update document"
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

    @ApiOperation(
        value = "Get document",
        notes = "Get document"
    )
    @GetMapping("/{documentId}")
    fun getDocument(@PathVariable("documentId") documentId: UUID): DocumentView {
        log("getDocument called with id $documentId")
        return mapToDocumentView(documentService.getDocument(documentId))
    }

    @ApiOperation(
        value = "Delete document",
        notes = "Delete document"
    )
    @DeleteMapping("/{documentId}")
    fun deleteDocument(@PathVariable("documentId") documentId: UUID) {
        log("deleteDocument called with id $documentId")
        documentService.deleteDocument(documentId)
    }

    @ApiOperation(
        value = "Create comment for a given document",
        notes = "Create comment for a given document"
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

    @ApiOperation(
        value = "Get all comments for a given document",
        notes = "Get all comments for a given document"
    )
    @GetMapping("/{documentId}/comments")
    fun getAllCommentsWithPossibleThreads(
        @PathVariable("documentId") documentId: UUID
    ): List<CommentView> {
        log("getAllCommentsWithPossibleThreads called with id $documentId")
        return commentService.getComments(documentId).map { mapCommentToView(it) }
    }

    @ApiOperation(
        value = "Reply to a given comment",
        notes = "Reply to a given comment"
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

    @ApiOperation(
        value = "Get a given comment",
        notes = "Get a given comment"
    )
    @GetMapping("/{documentId}/comments/{commentId}")
    fun getCommentWithPossibleThread(
        @PathVariable("documentId") documentId: UUID,
        @PathVariable("commentId") commentId: UUID
    ): CommentView {
        log("getCommentWithPossibleThread called with id $documentId and commentId $commentId")
        return mapCommentToView(commentService.getComment(commentId = commentId))
    }

    @ApiOperation(
        value = "Generer PDF",
        notes = "Generer PDF"
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
            templateId = document.templateId,
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