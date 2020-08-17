package model.veranstaltung.enum

import kotlinx.serialization.Serializable

enum class Kategorie(val id: Int, val desc: String) {
    UNKNOWN(1, "Unbekannt"),
    // Veranstaltungen an Hochschule
    SCHNUPPERWOCHE(10, "Schnupperwoche"),
    CAMPUSTAG(11, "Campustag"),
    SONSTIGES(12, "Sonstiges"),
    // Veranstaltungen an Schule
    VORTRAG(20, "Vortrag"),
    WORKSHOP(21, "Workshop");

    companion object {
        fun getKategorieByDesc(desc: String) = when (desc) {
            "Schnupperwoche" -> SCHNUPPERWOCHE
            "Campustag" -> CAMPUSTAG
            "Sonstiges" -> SONSTIGES
            "Vortrag" -> VORTRAG
            "Workshop" -> WORKSHOP
            else -> UNKNOWN
        }
    }
}

@Serializable
data class KategorieDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<KategorieDto> = Kategorie.values().map { KategorieDto(it.id, it.desc) }
    }
}