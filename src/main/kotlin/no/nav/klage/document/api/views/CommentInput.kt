package no.nav.klage.document.api.views

data class CommentInput(
    val text: String,
    val author: Author
) {
    data class Author(
        val name: String,
        val ident: String
    )
}

data class ModifyCommentInput(
    val text: String,
)

data class DeleteCommentInput(
    val behandlingTildeltIdent: String,
)
