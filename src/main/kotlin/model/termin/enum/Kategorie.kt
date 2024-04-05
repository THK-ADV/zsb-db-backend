package model.termin.enum

import kotlinx.serialization.Serializable

enum class Kategorie(val id: Int, val desc: String) {
    UNKNOWN(0, "Unbekannt"),
    SCHOOL(1, "AnSchuleTermin"),
    INTERN(2, "BeiUnsTermin"),
    THIRD(3, "BeiDrittenTermin");

    companion object {
        fun getById(id: Int) = when (id) {
            1 -> SCHOOL
            2 -> INTERN
            3 -> THIRD
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