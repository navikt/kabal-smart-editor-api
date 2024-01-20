package no.nav.klage.document.service

import no.nav.klage.document.domain.Document
import no.nav.klage.document.domain.DocumentVersion
import no.nav.klage.document.domain.DocumentVersionId
import no.nav.klage.document.repositories.CommentRepository
import no.nav.klage.document.repositories.DocumentRepository
import no.nav.klage.document.repositories.DocumentVersionRepository
import no.nav.klage.document.util.TokenUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class DocumentService(
    private val documentVersionRepository: DocumentVersionRepository,
    private val commentRepository: CommentRepository,
    private val documentRepository: DocumentRepository,
    private val tokenUtil: TokenUtil,
) {

    fun createDocument(json: String): DocumentVersion {
        val now = LocalDateTime.now()

        val document = documentRepository.save(
            Document(
                created = now,
                modified = now,
            )
        )

        return documentVersionRepository.save(
            DocumentVersion(
                documentId = document.id,
                version = 1,
                json = json,
                authorNavIdent = tokenUtil.getIdent(),
                created = now,
                modified = now,
            )
        )
    }

    fun updateDocument(documentId: UUID, json: String): DocumentVersion {
        val now = LocalDateTime.now()
        val latestVersionNumber = documentVersionRepository.findLatestVersionNumber(documentId = documentId)
        val documentVersion = documentVersionRepository.findByDocumentIdAndVersion(documentId = documentId, version = latestVersionNumber)
        return documentVersionRepository.save(
            DocumentVersion(
                documentId = documentVersion.documentId,
                version = documentVersion.version + 1,
                json = json,
                created = now,
                modified = now,
                authorNavIdent = tokenUtil.getIdent()
            )
        )
    }

    fun getDocument(documentId: UUID, version: Int?): DocumentVersion {
        val versionToUse = version ?: documentVersionRepository.findLatestVersionNumber(documentId = documentId)
        return documentVersionRepository.findById(DocumentVersionId(documentId = documentId, version = versionToUse)).get()
    }

    fun deleteDocument(documentId: UUID) {
        commentRepository.deleteByDocumentId(documentId)
        documentVersionRepository.deleteByDocumentId(documentId)
        documentRepository.deleteById(documentId)
    }

    fun getDocumentVersions(documentId: UUID): List<DocumentVersion> {
        return documentVersionRepository.findByDocumentId(documentId = documentId).sortedBy { it.version }
    }

}