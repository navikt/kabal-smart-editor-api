package no.nav.klage.document.service

import no.nav.klage.document.domain.Document
import no.nav.klage.document.repositories.DocumentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class DocumentService(private val documentRepository: DocumentRepository) {

    fun createDocument(json: String): Document {
        val now = LocalDateTime.now()
        return documentRepository.save(
            Document(
                json = json,
                created = now,
                modified = now
            )
        )
    }

    fun getDocument(documentId: UUID): Document {
        return documentRepository.getById(documentId)
    }

}