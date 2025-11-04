package no.nav.klage.document.repositories

import no.nav.klage.document.domain.Document
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface DocumentRepository : JpaRepository<Document, UUID>