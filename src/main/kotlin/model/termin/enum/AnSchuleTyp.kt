package model.termin.enum

import kotlinx.serialization.Serializable

enum class AnSchuleTyp(val id: Int, val desc: String) {
    OTHER(0, "Sonstiges"),
    KAOA(1, "KAoA"),
    TALENTSCOUT(2, "Talentscouting"),
    THSPECIFIC(3, "TH-spezifisch");
    companion object {
        fun getById(id: Int) = when (id) {
            1 -> KAOA
            2 -> TALENTSCOUT
            3 -> THSPECIFIC
            else -> OTHER
        }
    }
}

@Serializable
data class AnSchuleTypDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<AnSchuleTypDto> = AnSchuleTyp.values().map { AnSchuleTypDto(it.id, it.desc) }
    }
}