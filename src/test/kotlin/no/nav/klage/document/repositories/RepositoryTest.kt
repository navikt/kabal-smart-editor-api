package no.nav.klage.document.repositories

import no.nav.klage.document.domain.Comment
import no.nav.klage.document.domain.Document
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@ActiveProfiles("local")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var documentRepository: DocumentRepository

    @Autowired
    lateinit var commentRepository: CommentRepository

    @Test
    fun `add document and comments work`() {
        val now = LocalDateTime.now()

        val document = Document(
            json = "{}",
            templateId = "some_template",
            created = now,
            modified = now
        )

        documentRepository.save(document)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundDocument = documentRepository.findById(document.id).get()
        assertThat(foundDocument).isEqualTo(document)

        val comment1 = Comment(
            documentId = document.id,
            text = "my comment 1",
            authorName = "Kalle Anka",
            authorIdent = "Z123456",
            created = now.plusDays(1),
            modified = now.plusDays(1)
        )

        val comment2 = Comment(
            documentId = document.id,
            parentCommentId = comment1.id,
            text = "my sub comment 1",
            authorName = "Kajsa Anka",
            authorIdent = "Z654321",
            created = now.plusDays(2),
            modified = now.plusDays(2)
        )

        val comment3 = Comment(
            documentId = document.id,
            parentCommentId = comment1.id,
            text = "my sub comment 2",
            authorName = "Kajsa Anka",
            authorIdent = "Z654321",
            created = now.plusDays(3),
            modified = now.plusDays(3)
        )

        commentRepository.save(comment1)
        commentRepository.save(comment2)
        commentRepository.save(comment3)

        testEntityManager.flush()
        testEntityManager.clear()

        val comments = commentRepository.findByDocumentIdAndParentCommentIdIsNullOrderByCreatedAsc(document.id)

        assertThat(comments.first().comments).hasSize(2)
        assertThat(comments.first().comments.first()).isEqualTo(comment2)

        commentRepository.deleteByDocumentId(document.id)
        documentRepository.deleteById(document.id)

        testEntityManager.flush()
        testEntityManager.clear()

        assertThat(documentRepository.findAll()).isEmpty()
    }

}
