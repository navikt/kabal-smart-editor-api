package no.nav.klage.document.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "document_version", schema = "klage")
@IdClass(DocumentVersionId::class)
class DocumentVersion(
    @Id
    val documentId: UUID,
    @Id
    val version: Int,
    @Column(name = "json")
    var json: String,
    @Column(name = "data")
    var data: String?,
    @Column(name = "created")
    val created: LocalDateTime,
    @Column(name = "modified")
    var modified: LocalDateTime,
    @Column(name = "author_nav_ident")
    var authorNavIdent: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DocumentVersion

        if (documentId != other.documentId) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        return documentId.hashCode()
    }

    override fun toString(): String {
        return "DocumentVersion(documentId=$documentId, version=$version, json='$json', data=$data, created=$created, modified=$modified, authorNavIdent='$authorNavIdent')"
    }

}

/**
 * Using this when we don't need the full DocumentVersion object, just a subset of the fields.
 * Otherwise, we would have to fetch the full object from the database and that's too much data.
 */
data class ShortDocumentVersion(
    val documentId: UUID,
    val version: Int,
    val authorNavIdent: String,
    val created: LocalDateTime,
    val modified: LocalDateTime,
)
