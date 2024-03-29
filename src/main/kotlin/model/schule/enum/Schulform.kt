package model.schule.enum

import kotlinx.serialization.Serializable

enum class Schulform(val id: Int, val desc: String) {
    KEINE(0, "Keine"),
    GRUNDSCHULE(1, "Grundschule"),
    HAUPTSCHULE(2, "Hauptschule"),
    REALSCHULE(3, "Realschule"),
    GYMNASIUM(4, "Gymnasium"),
    GESAMTSCHULE(5, "Gesamtschule"),
    BERUFSKOLLEG(6, "Berufskolleg"),
    OTHER(7, "Andere");

    companion object {
        fun getDescById(id: Int): String? = values().find { it.id == id }?.desc

        fun fromDesc(desc: String): Schulform = when (desc) {
                "Keine" -> KEINE
                "Grundschule" -> GRUNDSCHULE
                "Hauptschule" -> HAUPTSCHULE
                "Realschule" -> REALSCHULE
                "Gymnasium" -> GYMNASIUM
                "Gesamtschule" -> GESAMTSCHULE
                "Berufskolleg" -> BERUFSKOLLEG
                else -> OTHER
        }
    }
}

@Serializable
data class SchulformDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<SchulformDto> = Schulform.values().map { SchulformDto(it.id, it.desc) }
    }
}
