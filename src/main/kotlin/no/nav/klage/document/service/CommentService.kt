package no.nav.klage.document.service

import no.nav.klage.document.domain.Comment
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
        return commentRepository.getById(commentId)
    }

}