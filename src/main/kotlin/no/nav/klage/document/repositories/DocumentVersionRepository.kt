package no.nav.klage.document.repositories

import no.nav.klage.document.domain.DocumentVersion
import no.nav.klage.document.domain.DocumentVersionId
import no.nav.klage.document.domain.ShortDocumentVersion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface DocumentVersionRepository : JpaRepository<DocumentVersion, DocumentVersionId> {

    fun findByDocumentId(documentId: UUID): List<DocumentVersion>

    @Query(
        """
        SELECT new no.nav.klage.document.domain.ShortDocumentVersion(documentId, version, authorNavIdent, created, modified)
        FROM DocumentVersion
        WHERE documentId = :documentId
        ORDER BY version
        """
    )
    fun findVersionsByDocumentId(documentId: UUID): List<ShortDocumentVersion>

    fun deleteByDocumentId(documentId: UUID)
}