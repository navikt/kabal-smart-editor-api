package no.nav.klage.document.config

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.context.annotation.Configuration

@EnableJwtTokenValidation(ignore = ["org.springdoc", "org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController"])
@Configuration
internal class SecurityConfiguration {

    companion object {
        const val ISSUER_AAD = "aad"
    }
}
