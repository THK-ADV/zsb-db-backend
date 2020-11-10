package model.veranstaltung.enum

import kotlinx.serialization.Serializable

enum class Stufe(val id: Int, val desc: String) {
    UNKNOWN(0, "Unbekannt"),
    EF(1, "EF"),
    Q1(2, "Q1"),
    Q2(3, "Q2"),
    SEK(4, "Sek. I");

    companion object {
        fun getStufeByDesc(desc: String) = when (desc) {
            "EF" -> EF
            "Q1" -> Q1
            "Q2" -> Q2
            "Sek. I" -> SEK
            else -> UNKNOWN
        }
    }
}

@Serializable
data class StufeDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<StufeDto> = Stufe.values().map { StufeDto(it.id, it.desc) }
    }
}