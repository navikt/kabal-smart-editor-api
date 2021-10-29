package no.nav.klage.document.domain

data class PDFDocument(
    val filename: String,
    val bytes: ByteArray
)