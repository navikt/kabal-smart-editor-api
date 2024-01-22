package no.nav.klage.document.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
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
        return "Document(id=$documentId, version=$version, json='$json', created=$created, modified=$modified, authorNavIdent='$authorNavIdent')"
    }

}
