package model.termin.enum

import kotlinx.serialization.Serializable

enum class TalentscoutTyp(val id: Int, val desc: String) {
    OTHER(0, "Sonstiges"),
    CONVERSATION(1, "Gespräch"),
    SCOUTING(2, "Scouting");
    companion object {
        fun getById(id: Int) = when (id) {
            1 -> CONVERSATION
            2 -> SCOUTING
            else -> OTHER
        }
    }
}

@Serializable
data class TalentscoutTypDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<TalentscoutTypDto> = TalentscoutTyp.values().map { TalentscoutTypDto(it.id, it.desc) }
    }
}