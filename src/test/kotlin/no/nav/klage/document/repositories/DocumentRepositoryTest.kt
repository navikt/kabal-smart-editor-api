package no.nav.klage.document.repositories

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
class DocumentRepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var documentRepository: DocumentRepository

    @Test
    fun `add documents works`() {

        val now = LocalDateTime.now()

        val document = Document(
            json = "{}",
            created = now,
            modified = now
        )

        documentRepository.save(document)

        testEntityManager.flush()
        testEntityManager.clear()

        val foundDocument = documentRepository.findById(document.id).get()
        assertThat(foundDocument).isEqualTo(document)
    }

}
