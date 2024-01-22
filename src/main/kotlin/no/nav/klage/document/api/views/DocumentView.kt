package no.nav.klage.document.api.views

import java.time.LocalDateTime
import java.util.*

data class DocumentView(
    val json: String,
    val documentId: UUID,
    val id: UUID,
    val version: Int,
    val authorNavIdent: String?,
    val created: LocalDateTime,
    val modified: LocalDateTime
)

data class DocumentVersionView(
    val documentId: UUID,
    val version: Int,
    val authorNavIdent: String?,
    val created: LocalDateTime,
    val modified: LocalDateTime,
)
