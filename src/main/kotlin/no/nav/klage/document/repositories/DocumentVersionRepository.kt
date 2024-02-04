package no.nav.klage.document.repositories

import no.nav.klage.document.domain.DocumentVersion
import no.nav.klage.document.domain.DocumentVersionId
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface DocumentVersionRepository : JpaRepository<DocumentVersion, DocumentVersionId> {

    fun findByDocumentId(documentId: UUID): List<DocumentVersion>

    fun deleteByDocumentId(documentId: UUID)
}