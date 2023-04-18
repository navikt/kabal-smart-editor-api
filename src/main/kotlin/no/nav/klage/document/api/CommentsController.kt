package no.nav.klage.document.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.document.api.views.CommentInput
import no.nav.klage.document.api.views.CommentView
import no.nav.klage.document.api.views.DeleteCommentInput
import no.nav.klage.document.api.views.ModifyCommentInput
import no.nav.klage.document.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.klage.document.domain.Comment
import no.nav.klage.document.service.CommentService
import no.nav.klage.document.util.getLogger
import no.nav.klage.document.util.getSecureLogger
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@ProtectedWithClaims(issuer = ISSUER_AAD)
@Tag(name = "kabal-smart-editor-api")
@RequestMapping("/documents/{documentId}/comments")
class CommentsController(
    private val commentService: CommentService,
    private val tokenValidationContextHolder: TokenValidationContextHolder
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    @Operation(
        summary = "Create comment for a given document",
        description = "Create comment for a given document"
    )
    @PostMapping
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
    @GetMapping
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
    @PostMapping("/{commentId}/replies")
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
        summary = "Modify a given comment",
        description = "Modify a given comment"
    )
    @PatchMapping("/{commentId}")
    fun modifyComment(
        @PathVariable("documentId") documentId: UUID,
        @PathVariable("commentId") commentId: UUID,
        @RequestBody modifyCommentInput: ModifyCommentInput,
    ): CommentView {
        log("modifyComment called with id $documentId and commentId $commentId")
        return mapCommentToView(
            commentService.setCommentText(
                commentId = commentId,
                text = modifyCommentInput.text,
                loggedInIdent = getIdent()!!
            )
        )
    }

    @Operation(
        summary = "Get a given comment",
        description = "Get a given comment"
    )
    @GetMapping("/{commentId}")
    fun getCommentWithPossibleThread(
        @PathVariable("documentId") documentId: UUID,
        @PathVariable("commentId") commentId: UUID
    ): CommentView {
        log("getCommentWithPossibleThread called with id $documentId and commentId $commentId")
        return mapCommentToView(commentService.getComment(commentId = commentId))
    }

    @Operation(
        summary = "Delete a given comment (includes possible thread)",
        description = "Delete a given comment (includes possible thread)"
    )
    @DeleteMapping("/{commentId}")
    fun deleteCommentWithPossibleThread(
        @PathVariable("documentId") documentId: UUID,
        @PathVariable("commentId") commentId: UUID,
        @RequestBody deleteCommentInput: DeleteCommentInput
    ) {
        log("deleteCommentWithPossibleThread called with id $documentId and commentId $commentId")
        commentService.deleteComment(commentId = commentId, loggedInIdent = getIdent()!!, behandlingTildeltIdent = deleteCommentInput.behandlingTildeltIdent)
    }

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