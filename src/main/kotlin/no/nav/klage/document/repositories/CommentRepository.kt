package no.nav.klage.document.repositories

import no.nav.klage.document.domain.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface CommentRepository : JpaRepository<Comment, UUID> {

    /**
     * Only find parent comments
     */
    fun findByDocumentIdAndParentCommentIdIsNullOrderByCreatedAsc(documentId: UUID): List<Comment>

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Comment WHERE documentId = :documentId")
    fun deleteByDocumentId(documentId: UUID)

}