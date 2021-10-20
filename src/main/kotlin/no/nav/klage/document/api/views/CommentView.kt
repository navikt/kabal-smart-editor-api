package no.nav.klage.document.api.views

import java.time.LocalDateTime
import java.util.*

data class CommentView(
    val id: UUID,
    val text: String,
    val author: Author,
    val comments: List<CommentView> = emptyList(),
    val created: LocalDateTime,
    val modified: LocalDateTime
) {
    data class Author(
        val name: String,
        val ident: String
    )
}