package no.nav.klage.document.util

import no.nav.klage.document.config.SecurityConfiguration.Companion.ISSUER_AAD
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.stereotype.Service

@Service
class TokenUtil(
    private val tokenValidationContextHolder: TokenValidationContextHolder,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun getIdent(): String =
        getIdentNullable() ?: throw RuntimeException("NAVident not found in token")

    fun getIdentNullable(): String? =
        tokenValidationContextHolder.getTokenValidationContext().getJwtToken(ISSUER_AAD)
            ?.jwtTokenClaims?.get("NAVident")?.toString()

}
