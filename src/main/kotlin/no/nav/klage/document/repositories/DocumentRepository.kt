package no.nav.klage.document.repositories

import no.nav.klage.document.domain.Document
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface DocumentRepository : JpaRepository<Document, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Document WHERE id = :documentId")
    fun deleteByDocumentId(documentId: UUID)
}