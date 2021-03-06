package no.nav.klage.document.domain

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "document", schema = "klage")
class Document(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "json")
    var json: String,
    @Column(name = "created")
    val created: LocalDateTime,
    @Column(name = "modified")
    var modified: LocalDateTime
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Document

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Document(id=$id, json='$json', created=$created, modified=$modified)"
    }

}
