package model.termin.enum

import kotlinx.serialization.Serializable

enum class Kategorie(val id: Int, val desc: String) {
    UNKNOWN(0, "Unbekannt"),
    TH_BESUCH(1, "Besuch an der TH"),
    EINZELBERATUNG(2, "Einzelberatungen"),
    MESSE(3, "Messe/Stand"),
    SCHNUPPERTAG(4, "Schnuppertag"),
    SCHNUPPERWOCHE(5, "Schnupperwoche"),
    VORTRAG_TH(6, "Vortrag an der TH"),
    VORTRAG_EXTERN(7, "Vortrag in der Einrichtung"),
    WORKSHOP_TH(8, "Workshop an der TH"),
    WORKSHOP_EXTERN(9, "Workshop in der Einrichtung");

    companion object {
        fun getKategorieByDesc(desc: String) = when (desc) {
            "Unbekannt" -> TH_BESUCH
            "Besuch an der TH" -> EINZELBERATUNG
            "Einzelberatungen" -> MESSE
            "Messe/Stand" -> SCHNUPPERTAG
            "Schnuppertag" -> SCHNUPPERWOCHE
            "Schnupperwoche" -> VORTRAG_TH
            "Vortrag an der TH" -> VORTRAG_EXTERN
            "Vortrag in der Einrichtung" -> WORKSHOP_TH
            "Workshop an der TH" -> WORKSHOP_EXTERN
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