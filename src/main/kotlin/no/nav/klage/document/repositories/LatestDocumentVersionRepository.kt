package no.nav.klage.document.repositories

import no.nav.klage.document.domain.LatestDocumentVersion
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface LatestDocumentVersionRepository : JpaRepository<LatestDocumentVersion, UUID>