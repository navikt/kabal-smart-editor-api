package no.nav.klage.document.api.views

data class DocumentUpdateInput(
    val json: String,
    val currentVersion: Int?,
)