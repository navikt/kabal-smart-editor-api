package no.nav.klage.document.service

import no.nav.klage.document.clients.KabalJsonToPdfClient
import no.nav.klage.document.domain.Document
import no.nav.klage.document.domain.PDFDocument
import no.nav.klage.document.repositories.DocumentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class DocumentService(
    private val documentRepository: DocumentRepository,
    private val kabalJsonToPdfClient: KabalJsonToPdfClient
) {

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

    fun updateDocument(documentId: UUID, json: String): Document {
        val document = documentRepository.getById(documentId)
        document.json = json
        document.modified = LocalDateTime.now()
        return document
    }

    fun getDocument(documentId: UUID): Document {
        return documentRepository.getById(documentId)
    }

    fun getDocumentAsPDF(documentId: UUID): PDFDocument {
        return kabalJsonToPdfClient.getPDFDocument(documentRepository.getById(documentId).json)
    }

}