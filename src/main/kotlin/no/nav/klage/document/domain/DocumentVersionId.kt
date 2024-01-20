package no.nav.klage.document.domain

import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.*

@Embeddable
data class DocumentVersionId(
    val documentId: UUID,
    val version: Int = 1,
): Serializable