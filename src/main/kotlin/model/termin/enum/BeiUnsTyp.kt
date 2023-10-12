package model.termin.enum

import kotlinx.serialization.Serializable

enum class BeiUnsTyp(val id: Int, val desc: String) {
    OTHER(0, "Sonstiges"),
    CAMPUSDAY(1, "Campustag(e)"),
    LAB(2, "Schülerlabor");
    companion object {
        fun getById(id: Int) = when (id) {
            1 -> CAMPUSDAY
            2 -> LAB
            else -> OTHER
        }
    }
}

@Serializable
data class BeiUnsTypDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<BeiUnsTypDto> = BeiUnsTyp.values().map { BeiUnsTypDto(it.id, it.desc) }
    }
}