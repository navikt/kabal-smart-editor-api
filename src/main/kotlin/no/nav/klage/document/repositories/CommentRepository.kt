package no.nav.klage.document.repositories

import no.nav.klage.document.domain.Comment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CommentRepository : JpaRepository<Comment, UUID> {

    fun findByDocumentId(documentId: UUID): List<Comment>

}