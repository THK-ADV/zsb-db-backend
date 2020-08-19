package model.kontakt.enum

import kotlinx.serialization.Serializable

enum class Anrede(val id: Int, val desc: String) {
    UNKNOWN(0, ""),
    HERR(1, "Herr"),
    FRAU(2, "Frau");

    companion object {
        fun getObjectByString(string: String) = when (string) {
            "Herr" -> HERR
            "Frau" -> FRAU
            else -> UNKNOWN
        }
    }
}

@Serializable
data class AnredeDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<AnredeDto> = Anrede.values().map { AnredeDto(it.id, it.desc) }
    }
}