package no.nav.klage.document.api

import java.time.LocalDateTime
import java.util.*

data class CommentView(
    val id: UUID,
    val text: String,
    val created: LocalDateTime,
    val modified: LocalDateTime
)
