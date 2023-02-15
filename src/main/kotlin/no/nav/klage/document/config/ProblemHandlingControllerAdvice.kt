package no.nav.klage.document.config

import no.nav.klage.document.exceptions.MissingAccessException
import no.nav.klage.document.util.getSecureLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ProblemHandlingControllerAdvice : ResponseEntityExceptionHandler() {

    companion object {
        private val secureLogger = getSecureLogger()
    }

    @ExceptionHandler
    fun handleEntityNotFound(
        ex: JpaObjectRetrievalFailureException,
        request: NativeWebRequest
    ): ProblemDetail =
        create(HttpStatus.NOT_FOUND, ex)

    @ExceptionHandler
    fun handleMissingAccess(
        ex: MissingAccessException,
        request: NativeWebRequest
    ): ProblemDetail =
        create(HttpStatus.FORBIDDEN, ex)

    private fun create(httpStatus: HttpStatus, ex: Exception): ProblemDetail {
        val errorMessage = ex.message ?: "No error message available"

        logError(
            httpStatus = httpStatus,
            errorMessage = errorMessage,
            exception = ex
        )

        return ProblemDetail.forStatusAndDetail(httpStatus, errorMessage).apply {
            title = errorMessage
        }
    }

    private fun logError(httpStatus: HttpStatus, errorMessage: String, exception: Exception) {
        when {
            httpStatus.is5xxServerError -> {
                secureLogger.error("Exception thrown to client: ${httpStatus.reasonPhrase}, $errorMessage", exception)
            }

            else -> {
                secureLogger.warn("Exception thrown to client: ${httpStatus.reasonPhrase}, $errorMessage", exception)
            }
        }
    }
}