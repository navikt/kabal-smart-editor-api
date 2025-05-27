package no.nav.klage.document.service

import no.nav.klage.document.api.views.DocumentVersionView
import no.nav.klage.document.api.views.DocumentView
import no.nav.klage.document.domain.Document
import no.nav.klage.document.domain.DocumentVersion
import no.nav.klage.document.domain.DocumentVersionId
import no.nav.klage.document.domain.ShortDocumentVersion
import no.nav.klage.document.repositories.CommentRepository
import no.nav.klage.document.repositories.DocumentRepository
import no.nav.klage.document.repositories.DocumentVersionRepository
import no.nav.klage.document.repositories.LatestDocumentVersionRepository
import no.nav.klage.document.util.TokenUtil
import no.nav.klage.document.util.getLogger
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
    }

    fun createDocument(json: String, data: String?): DocumentView {
        val now = LocalDateTime.now()

        val document = documentRepository.save(
            Document(
                data = data,
                created = now,
                modified = now,
            )
        )

        return mapToDocumentView(
            documentVersionRepository.save(
                DocumentVersion(
                    documentId = document.id,
                    version = 1,
                    json = json,
                    authorNavIdent = tokenUtil.getIdent(),
                    created = now,
                    modified = now,
                )
            ),
            document = document
        )
    }

    fun updateDocument(documentId: UUID, json: String, data: String?, currentVersion: Int?): DocumentView {
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

        val document = documentRepository.findById(documentId).get()
        document.data = data
        document.modified = now

        return mapToDocumentView(
            documentVersion = documentVersionRepository.save(
                DocumentVersion(
                    documentId = documentVersion.documentId,
                    version = latestVersionNumber + 1,
                    json = json,
                    created = now,
                    modified = now,
                    authorNavIdent = tokenUtil.getIdent()
                )
            ),
            document = document
        )
    }

    fun getDocument(documentId: UUID, version: Int?): DocumentView {
        val versionToUse = version ?: latestDocumentRepository.findById(documentId).get().currentVersion
        return mapToDocumentView(
            documentVersion = documentVersionRepository.findById(
                DocumentVersionId(
                    documentId = documentId,
                    version = versionToUse
                )
            ).get(),
            document = documentRepository.findById(documentId).get()
        )
    }

    fun deleteDocument(documentId: UUID) {
        commentRepository.deleteByDocumentId(documentId)
        documentVersionRepository.deleteByDocumentId(documentId)
        latestDocumentRepository.deleteById(documentId)
        documentRepository.deleteById(documentId)
    }

    fun getDocumentVersions(documentId: UUID): List<DocumentVersionView> {
        return documentVersionRepository.findVersionsByDocumentId(documentId = documentId)
            .map { mapToDocumentVersionView(it) }
    }

    private fun mapToDocumentView(documentVersion: DocumentVersion, document: Document): DocumentView =
        DocumentView(
            id = documentVersion.documentId,
            documentId = documentVersion.documentId,
            version = documentVersion.version,
            json = documentVersion.json,
            data = document.data,
            authorNavIdent = documentVersion.authorNavIdent,
            created = documentVersion.created,
            modified = documentVersion.modified
        )

    private fun mapToDocumentVersionView(documentVersion: ShortDocumentVersion): DocumentVersionView =
        DocumentVersionView(
            documentId = documentVersion.documentId,
            version = documentVersion.version,
            authorNavIdent = documentVersion.authorNavIdent,
            created = documentVersion.created,
            modified = documentVersion.modified
        )

}