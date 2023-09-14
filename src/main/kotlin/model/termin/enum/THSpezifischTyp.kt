package model.termin.enum

import kotlinx.serialization.Serializable

enum class THSpezifischTyp(val id: Int, val desc: String) {
    OTHER(0, "Sonstiges"),
    CONSULTATION(1, "Beratung"),
    TALK(2, "Fachvortrag"),
    THTALK(3, "Vortrag 'Technology, Arts, Sciences'"),
    INFORMATION(4, "Informationsstand");
    companion object {
        fun getById(id: Int) = when (id) {
            1 -> CONSULTATION
            2 -> TALK
            3 -> THTALK
            4 -> INFORMATION
            else -> OTHER
        }
    }
}

@Serializable
data class THSpezifischTypDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<THSpezifischTypDto> = THSpezifischTyp.values().map { THSpezifischTypDto(it.id, it.desc) }
    }
}