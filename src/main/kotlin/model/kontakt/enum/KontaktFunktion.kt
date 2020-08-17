package model.kontakt.enum

import kotlinx.serialization.Serializable

enum class KontaktFunktion(val id: Int, val desc: String) {
    SCHULLEITUNG(1, "Schulleitung"),
    STUBO(2, "StuBo"),
    SECRETARIAT(3, "Sekretariat"),
    OTHER(4, "Sonstiges"),
    UNKNOWN(5, "Unbekannt");

    companion object {
        fun getFunktionByDesc(desc: String) = when (desc) {
            "Schulleitung" -> SCHULLEITUNG
            "StuBo" -> STUBO
            "Sekretariat" -> SECRETARIAT
            "Sonstiges" -> OTHER
            else -> UNKNOWN
        }
    }
}

@Serializable
data class KontaktFunktionDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<KontaktFunktionDto> {
            val list = mutableListOf<KontaktFunktionDto>()
            KontaktFunktion.values().forEach { list.add(
                KontaktFunktionDto(
                    it.id,
                    it.desc
                )
            ) }
            return list
        }
    }
}