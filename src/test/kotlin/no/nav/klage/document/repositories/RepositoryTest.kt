package no.nav.klage.document.repositories

import no.nav.klage.document.domain.Comment
import no.nav.klage.document.domain.Document
import no.nav.klage.document.domain.DocumentVersion
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
    lateinit var documentVersionRepository: DocumentVersionRepository

    @Autowired
    lateinit var latestDocumentVersionRepository: LatestDocumentVersionRepository

    @Autowired
    lateinit var documentRepository: DocumentRepository

    @Autowired
    lateinit var commentRepository: CommentRepository

    @Test
    fun `add documentVersion and comments work`() {
        val now = LocalDateTime.now()

        val document = testEntityManager.persistAndFlush(
            Document(
                data = "{}",
                created = now,
                modified = now,
            )
        )

        val documentVersion = testEntityManager.persistAndFlush(
            DocumentVersion(
                documentId = document.id,
                version = 1,
                authorNavIdent = "abc",
                json = "{}",
                created = now,
                modified = now,
            )
        )

        testEntityManager.clear()

        val foundDocumentVersion = documentVersionRepository.findByDocumentId(documentId = documentVersion.documentId)
        assertThat(foundDocumentVersion.first()).isEqualTo(documentVersion)

        val comment1Parent = testEntityManager.persistAndFlush(
            Comment(
                documentId = document.id,
                text = "my comment 1",
                authorName = "Kalle Anka",
                authorIdent = "Z123456",
                created = now.plusDays(1),
                modified = now.plusDays(1)
            )
        )

        val comment2 = testEntityManager.persistAndFlush(
            Comment(
                documentId = document.id,
                parentCommentId = comment1Parent.id,
                text = "my sub comment 1",
                authorName = "Kajsa Anka",
                authorIdent = "Z654321",
                created = now.plusDays(2),
                modified = now.plusDays(2)
            )
        )

        val comment3 = testEntityManager.persistAndFlush(
            Comment(
                documentId = document.id,
                parentCommentId = comment1Parent.id,
                text = "my sub comment 2",
                authorName = "Kajsa Anka",
                authorIdent = "Z654321",
                created = now.plusDays(3),
                modified = now.plusDays(3)
            )
        )

        testEntityManager.clear()

        val comments =
            commentRepository.findByDocumentIdAndParentCommentIdIsNullOrderByCreatedAsc(documentId = document.id)

        assertThat(comments.first().comments).hasSize(2)
        assertThat(comments.first().comments.first()).isEqualTo(comment2)

        commentRepository.deleteByDocumentId(document.id)
        documentVersionRepository.deleteByDocumentId(document.id)

        testEntityManager.flush()
        testEntityManager.clear()

        assertThat(documentVersionRepository.findAll()).isEmpty()
    }

    @Test
    fun `child comments are removed with parent`() {
        val now = LocalDateTime.now()

        val document = testEntityManager.persistAndFlush(
            Document(
                data = "{}",
                created = now,
                modified = now,
            )
        )

        val comment1Parent = testEntityManager.persistAndFlush(
            Comment(
                documentId = document.id,
                text = "my comment 1",
                authorName = "Kalle Anka",
                authorIdent = "Z123456",
                created = now.plusDays(1),
                modified = now.plusDays(1)
            )
        )

        val comment2 = testEntityManager.persistAndFlush(
            Comment(
                documentId = document.id,
                parentCommentId = comment1Parent.id,
                text = "my sub comment 1",
                authorName = "Kajsa Anka",
                authorIdent = "Z654321",
                created = now.plusDays(2),
                modified = now.plusDays(2)
            )
        )

        val comment3 = testEntityManager.persistAndFlush(
            Comment(
                documentId = document.id,
                parentCommentId = comment1Parent.id,
                text = "my sub comment 2",
                authorName = "Kajsa Anka",
                authorIdent = "Z654321",
                created = now.plusDays(3),
                modified = now.plusDays(3),
            )
        )

        testEntityManager.clear()

        val comments =
            commentRepository.findByDocumentIdAndParentCommentIdIsNullOrderByCreatedAsc(documentId = document.id)

        assertThat(comments.first().comments).hasSize(2)
        assertThat(comments.first().comments.first()).isEqualTo(comment2)

        commentRepository.deleteById(comment1Parent.id)
        documentRepository.deleteById(document.id)

        testEntityManager.flush()
        testEntityManager.clear()

        assertThat(commentRepository.findAll()).isEmpty()
        assertThat(documentRepository.findAll()).isEmpty()
    }

    @Test
    fun `add and delete document versions`() {
        val now = LocalDateTime.now()

        val document = testEntityManager.persistAndFlush(
            Document(
                data = "{}",
                created = now,
                modified = now,
            )
        )

        testEntityManager.persistAndFlush(
            DocumentVersion(
                documentId = document.id,
                version = 1,
                authorNavIdent = "abc",
                json = "{}",
                created = now,
                modified = now,
            )
        )

        testEntityManager.persistAndFlush(
            DocumentVersion(
                documentId = document.id,
                version = 2,
                authorNavIdent = "abc",
                json = "{}",
                created = now,
                modified = now,
            )
        )

        testEntityManager.clear()

        val founddocumentVersions = documentVersionRepository.findByDocumentId(documentId = document.id)
        assertThat(founddocumentVersions).hasSize(2)

        documentVersionRepository.deleteByDocumentId(documentId = document.id)

        testEntityManager.flush()
        testEntityManager.clear()

        assertThat(documentVersionRepository.findAll()).isEmpty()
    }

    @Test
    fun `latest version number is recorded`() {
        val now = LocalDateTime.now()

        val document1 = testEntityManager.persistAndFlush(
            Document(
                data = "{}",
                created = now,
                modified = now,
            )
        )

        val document2 = testEntityManager.persistAndFlush(
            Document(
                data = "{}",
                created = now,
                modified = now,
            )
        )

        val numberOfDocumenVersionsToCreateDocument1 = 10
        val numberOfDocumenVersionsToCreateDocument2 = 15

        repeat(numberOfDocumenVersionsToCreateDocument1) {
            testEntityManager.persistAndFlush(
                DocumentVersion(
                    documentId = document1.id,
                    version = it + 1,
                    authorNavIdent = "abc",
                    json = "{}",
                    created = now,
                    modified = now,
                )
            )
        }

        repeat(numberOfDocumenVersionsToCreateDocument2) {
            testEntityManager.persistAndFlush(
                DocumentVersion(
                    documentId = document2.id,
                    version = it + 1,
                    authorNavIdent = "abc",
                    json = "{}",
                    created = now,
                    modified = now,
                )
            )
        }

        testEntityManager.clear()

        val latestVersionNumberDocument1 = latestDocumentVersionRepository.findById(document1.id).get().currentVersion
        val latestVersionNumberDocument2 = latestDocumentVersionRepository.findById(document2.id).get().currentVersion

        assertThat(latestVersionNumberDocument1).isEqualTo(numberOfDocumenVersionsToCreateDocument1)
        assertThat(latestVersionNumberDocument2).isEqualTo(numberOfDocumenVersionsToCreateDocument2)
    }

}