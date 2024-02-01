package no.nav.klage.document.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "latest_document_version", schema = "klage")
class LatestDocumentVersion(
    @Id
    val documentId: UUID,
    val currentVersion: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LatestDocumentVersion

        return documentId == other.documentId
    }

    override fun hashCode(): Int {
        return documentId.hashCode()
    }

    override fun toString(): String {
        return "Document(id=$documentId, currentVersion=$currentVersion)"
    }

}
