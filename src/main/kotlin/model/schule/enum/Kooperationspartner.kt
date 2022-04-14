package model.schule.enum

import kotlinx.serialization.Serializable

enum class Kooperationspartner(val id: Int, val desc: String) {
    KEINE(0, "Keine"),
    TH(1, "TH Köln"),
    UNI(2, "Uni Köln"),
    OTHER(3, "Andere");

    companion object {

        fun fromDesc(desc: String): Kooperationspartner = when (desc) {
            "Keine" -> Kooperationspartner.KEINE
            "TH Köln" -> Kooperationspartner.TH
            "Uni Köln" -> Kooperationspartner.UNI
            else -> Kooperationspartner.OTHER
        }
    }
}

@Serializable
data class KooperationspartnerDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<KooperationspartnerDto> = Kooperationspartner.values().map {
            KooperationspartnerDto(it.id, it.desc)
        }
    }
}