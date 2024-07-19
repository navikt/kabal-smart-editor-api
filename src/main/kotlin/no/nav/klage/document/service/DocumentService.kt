package no.nav.klage.document.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.klage.document.domain.Document
import no.nav.klage.document.domain.DocumentVersion
import no.nav.klage.document.domain.DocumentVersionId
import no.nav.klage.document.repositories.CommentRepository
import no.nav.klage.document.repositories.DocumentRepository
import no.nav.klage.document.repositories.DocumentVersionRepository
import no.nav.klage.document.repositories.LatestDocumentVersionRepository
import no.nav.klage.document.util.TokenUtil
import no.nav.klage.document.util.getLogger
import no.nav.klage.document.util.getSecureLogger
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
    private val latestDocumentRepository: LatestDocumentVersionRepository,
    private val tokenUtil: TokenUtil,

    ) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }

    //find and log all documents with invalid json
    fun logDocumentsWithInvalidJson() {
        logger.debug("Checking for documents with invalid json")
        var counter = 0
        documentVersionRepository.findAll().forEach {
            try {
                jacksonObjectMapper().readTree(it.json)
            } catch (e: Exception) {
                counter++
                logger.debug(
                    "Document with id {} and version {}, modified {} has invalid json.",
                    it.documentId,
                    it.version,
                    it.modified,
                )
            }
        }
        logger.debug("Found {} documents with invalid json", counter)
    }

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

    fun updateDocument(documentId: UUID, json: String, currentVersion: Int?): DocumentVersion {
        val now = LocalDateTime.now()
        val latestVersionNumber = latestDocumentRepository.findById(documentId).get().currentVersion

        if (currentVersion != null && latestVersionNumber != currentVersion) {
            logger.warn(
                "latest db version {} and current client version {} does not match. Author: {}, documentId: {}",
                latestVersionNumber,
                currentVersion,
                tokenUtil.getIdentNullable(),
                documentId,
            )
        } else {
            logger.debug(
                "latest db version {} and current client version {} matches. Author: {}, documentId: {}",
                latestVersionNumber,
                currentVersion,
                tokenUtil.getIdentNullable(),
                documentId,
            )
        }

        val documentVersion =
            documentVersionRepository.findById(
                DocumentVersionId(
                    documentId = documentId,
                    version = latestVersionNumber
                )
            ).get()
        return documentVersionRepository.save(
            DocumentVersion(
                documentId = documentVersion.documentId,
                version = latestVersionNumber + 1,
                json = json,
                created = now,
                modified = now,
                authorNavIdent = tokenUtil.getIdent()
            )
        )
    }

    fun getDocument(documentId: UUID, version: Int?): DocumentVersion {
        val versionToUse = version ?: latestDocumentRepository.findById(documentId).get().currentVersion
        return documentVersionRepository.findById(
            DocumentVersionId(
                documentId = documentId,
                version = versionToUse
            )
        ).get()
    }

    fun deleteDocument(documentId: UUID) {
        commentRepository.deleteByDocumentId(documentId)
        documentVersionRepository.deleteByDocumentId(documentId)
        latestDocumentRepository.deleteById(documentId)
        documentRepository.deleteById(documentId)
    }

    fun getDocumentVersions(documentId: UUID): List<DocumentVersion> {
        return documentVersionRepository.findByDocumentId(documentId = documentId).sortedBy { it.version }
    }

}