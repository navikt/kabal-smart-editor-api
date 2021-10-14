package no.nav.klage.document.api

import no.nav.klage.document.util.getLogger
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

}