package no.nav.klage.document.util

import no.nav.klage.auth.currentAzureToken
import no.nav.klage.auth.navIdent
import org.springframework.stereotype.Service

@Service
class TokenUtil {
    fun getIdent(): String = getIdentNullable() ?: throw RuntimeException("NAVident not found in token")

    fun getIdentNullable(): String? = currentAzureToken()?.navIdent
}
