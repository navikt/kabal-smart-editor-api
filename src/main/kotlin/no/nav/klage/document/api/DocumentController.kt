package no.nav.klage.document.api

import no.nav.klage.document.util.getLogger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class DocumentController() {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @PostMapping("/documents")
    fun createDocument(
        @RequestBody json: String
    ): DocumentView {
        logger.debug("createDocument: received json: {}", json)
        TODO()
    }

    @GetMapping("/documents/{documentId}")
    fun getDocument(): DocumentView {
        logger.debug("getDocument")
        TODO()
    }

    @PostMapping("/documents/{documentId}/commentthreads")
    fun createCommentThread() {
        logger.debug("createCommentThread")
        TODO()
    }

    @GetMapping("/documents/{documentId}/commentthreads")
    fun getAllThreadsWithComments() {
        logger.debug("getAllThreadsWithComments")
        TODO()
    }

    @PostMapping("/documents/{documentId}/commentthreads/{threadId}")
    fun createCommentInThread() {
        logger.debug("createCommentInThread")
        TODO()
    }

    @GetMapping("/documents/{documentId}/commentthreads/{threadId}")
    fun getCommentsInThread() {
        logger.debug("getCommentsInThread")
        TODO()
    }

}