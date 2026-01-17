package no.nav.klage.document.repositories

import no.nav.klage.document.domain.LatestDocumentVersion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface LatestDocumentVersionRepository : JpaRepository<LatestDocumentVersion, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM LatestDocumentVersion WHERE documentId = :documentId")
    fun deleteByDocumentId(documentId: UUID)
}