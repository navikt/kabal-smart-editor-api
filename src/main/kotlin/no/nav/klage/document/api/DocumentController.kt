package no.nav.klage.document.api

import io.swagger.annotations.ApiOperation
import no.nav.klage.document.api.views.CommentInput
import no.nav.klage.document.api.views.CommentView
import no.nav.klage.document.api.views.DocumentView
import no.nav.klage.document.domain.Comment
import no.nav.klage.document.domain.Document
import no.nav.klage.document.service.CommentService
import no.nav.klage.document.service.DocumentService
import no.nav.klage.document.util.getLogger
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/documents")
class DocumentController(
    private val documentService: DocumentService,
    private val commentService: CommentService
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @ApiOperation(
        value = "Create document",
        notes = "Create document"
    )
    @PostMapping("")
    fun createDocument(
        @RequestBody json: String
    ): DocumentView {
        logger.debug("createDocument: received json: {}", json)
        return mapToDocumentView(documentService.createDocument(json))
    }

    @ApiOperation(
        value = "Get document",
        notes = "Get document"
    )
    @GetMapping("/{documentId}")
    fun getDocument(@PathVariable("documentId") documentId: UUID): DocumentView {
        logger.debug("getDocument")
        return mapToDocumentView(documentService.getDocument(documentId))
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
        logger.debug("createComment")
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
        logger.debug("getAllCommentsWithPossibleThreads")
        return commentService.getComments(documentId).map { mapCommentToView(it) }
    }

    @ApiOperation(
        value = "Reply to a given comment",
        notes = "Reply to a given comment"
    )
    @PostMapping("/{documentId}/comments/{commentId}")
    fun replyToComment(
        @PathVariable("documentId") documentId: UUID,
        @PathVariable("commentId") commentId: UUID,
        @RequestBody commentInput: CommentInput,
    ): CommentView {
        logger.debug("replyToComment")
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
        logger.debug("getCommentWithPossibleThread")
        return mapCommentToView(commentService.getComment(commentId = commentId))
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

}