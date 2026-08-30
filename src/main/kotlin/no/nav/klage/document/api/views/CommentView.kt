package no.nav.klage.document.api.views

import java.time.LocalDateTime
import java.util.UUID

data class CommentView(
    val id: UUID,
    val text: String,
    val author: Author,
    val comments: List<CommentView> = emptyList(),
    val created: LocalDateTime,
    val modified: LocalDateTime,
    val parentId: UUID?,
) {
    data class Author(
        val name: String,
        val ident: String,
    )
}
