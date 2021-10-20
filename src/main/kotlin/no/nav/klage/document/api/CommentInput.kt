package no.nav.klage.document.api

data class CommentInput(
    val text: String,
    val author: Author
) {
    data class Author(
        val name: String,
        val ident: String
    )
}
