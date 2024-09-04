package no.nav.klage.document.service

import no.nav.klage.document.domain.Comment
import no.nav.klage.document.exceptions.MissingAccessException
import no.nav.klage.document.repositories.CommentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class CommentService(private val commentRepository: CommentRepository) {

    fun createComment(documentId: UUID, text: String, authorName: String, authorIdent: String): Comment {
        val now = LocalDateTime.now()
        return commentRepository.save(
            Comment(
                documentId = documentId,
                text = text,
                authorName = authorName,
                authorIdent = authorIdent,
                created = now,
                modified = now
            )
        )
    }

    fun replyToComment(
        documentId: UUID,
        parentCommentId: UUID,
        text: String,
        authorName: String,
        authorIdent: String
    ): Comment {
        val now = LocalDateTime.now()
        return commentRepository.save(
            Comment(
                documentId = documentId,
                parentCommentId = parentCommentId,
                text = text,
                authorName = authorName,
                authorIdent = authorIdent,
                created = now,
                modified = now
            )
        )
    }

    fun getComments(documentId: UUID): List<Comment> {
        return commentRepository.findByDocumentIdAndParentCommentIdIsNullOrderByCreatedAsc(documentId)
    }

    fun getComment(commentId: UUID): Comment {
        return commentRepository.getReferenceById(commentId)
    }

    fun setCommentText(commentId: UUID, text: String, loggedInIdent: String): Comment {
        val comment = commentRepository.getReferenceById(commentId)
        if (comment.authorIdent != loggedInIdent) {
            throw MissingAccessException("Not allowed to modify others comment")
        }
        comment.text = text
        comment.modified = LocalDateTime.now()
        return comment
    }

    fun deleteComment(
        commentId: UUID,
        loggedInIdent: String,
        behandlingTildeltIdent: String?
    ): Comment {
        val loggedInIsDocumentOwner = loggedInIdent == behandlingTildeltIdent
        val comment = commentRepository.getReferenceById(commentId)
        if (!loggedInIsDocumentOwner && comment.authorIdent != loggedInIdent) {
            throw MissingAccessException("Not allowed to delete others comment when not document owner")
        }

        val commentCopy = Comment(
            id = commentId,
            parentCommentId = comment.parentCommentId,
            documentId = comment.documentId,
            text = "",
            authorName = comment.authorName,
            authorIdent = comment.authorIdent,
            comments = comment.comments,
            created = comment.created,
            modified = LocalDateTime.now(),
        )
        commentRepository.delete(comment)

        return commentCopy
    }
}