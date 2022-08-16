package model.termin.enum

import kotlinx.serialization.Serializable

enum class Vortragsart(val id: Int, val desc: String) {
    STUDIENANGEBOT(1, "Vortrag zum Studienangebot der TH Köln"),
    STUDIENTHEMEN(2, "Vortrag zu Themen der Allgemeinen Studienorientierung"),
    SONSTIGES(3, "Vortrag zu sonstigen Themen"),
    UNKNOWN(9, "Unbekannt");

    companion object {
        fun getVortragsartByDesc(desc: String) = when (desc) {
            "Vortrag zum Studienangebot der TH Köln" -> STUDIENANGEBOT
            "Vortrag zu Themen der Allgemeinen Studienorientierung" -> STUDIENTHEMEN
            "Vortrag zu sonstigen Themen" -> SONSTIGES
            else -> UNKNOWN
        }
    }
}

@Serializable
data class VortragsartDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<VortragsartDto> = Vortragsart.values().map { VortragsartDto(it.id, it.desc) }
    }
}