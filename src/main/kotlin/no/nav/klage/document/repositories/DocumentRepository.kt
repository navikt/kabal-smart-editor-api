package no.nav.klage.document.repositories

import no.nav.klage.document.domain.Document
import no.nav.klage.document.domain.DocumentVersion
import no.nav.klage.document.domain.DocumentVersionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface DocumentRepository : JpaRepository<Document, UUID>