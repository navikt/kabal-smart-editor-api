package no.nav.klage.document.domain

import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "comment", schema = "klage")
class Comment(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "parent_comment_id")
    var parentCommentId: UUID? = null,
    @Column(name = "document_id")
    var documentId: UUID,
    @Column(name = "text")
    var text: String,
    @Column(name = "author_name")
    var authorName: String,
    @Column(name = "author_ident")
    var authorIdent: String,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_comment_id", referencedColumnName = "id")
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    @OrderBy("created asc")
    val comments: MutableSet<Comment> = mutableSetOf(),
    @Column(name = "created")
    val created: LocalDateTime,
    @Column(name = "modified")
    var modified: LocalDateTime
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Comment

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Comment(id=$id, documentId=$documentId, text='$text', created=$created, modified=$modified)"
    }

}
