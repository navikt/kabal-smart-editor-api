package no.nav.klage.document.api

import java.time.LocalDateTime
import java.util.*

data class DocumentView(
    val id: UUID,
    val json: String,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
