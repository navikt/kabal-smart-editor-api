package no.nav.klage.document.exceptions

class MissingAccessException(msg: String) : RuntimeException(msg)

class ValidationException(msg: String): RuntimeException(msg)