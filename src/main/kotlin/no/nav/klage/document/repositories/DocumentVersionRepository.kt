package no.nav.klage.document.repositories

import no.nav.klage.document.domain.DocumentVersion
import no.nav.klage.document.domain.DocumentVersionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface DocumentVersionRepository : JpaRepository<DocumentVersion, DocumentVersionId> {

    fun findByDocumentId(documentId: UUID): List<DocumentVersion>

    fun findByDocumentIdAndVersion(documentId: UUID, version: Int): DocumentVersion

    @Query(
        """
            select max(dv.version) from DocumentVersion dv
            where dv.documentId = :documentId
        """
    )
    fun findLatestVersionNumber(documentId: UUID): Int

    fun deleteByDocumentId(documentId: UUID)
}