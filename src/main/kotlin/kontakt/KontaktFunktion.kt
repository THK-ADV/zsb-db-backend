package kontakt

import kotlinx.serialization.Serializable

enum class KontaktFunktion(val id: Int, val desc: String) {
    SCHULLEITUNG(1, "Schulleitung"),
    STUBO(2, "StuBo"),
    OTHER(3, "Sonstiges"),
    UNKNOWN(4, "Unbekannt")
}

@Serializable
data class KontaktFunktionDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<KontaktFunktionDto> {
            val list = mutableListOf<KontaktFunktionDto>()
            KontaktFunktion.values().forEach { list.add(KontaktFunktionDto(it.id, it.desc)) }
            return list
        }
    }
}