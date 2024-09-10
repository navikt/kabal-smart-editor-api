package no.nav.klage.document.api.views

data class DocumentUpdateInput(
    val json: String,
    val data: String?,
    val currentVersion: Int?,
)