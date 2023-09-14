package model.termin.enum

import kotlinx.serialization.Serializable

enum class KAoATyp(val id: Int, val desc: String) {
    OTHER(0, "Sonstiges"),
    LASTMINUTE(1, "Last Minute Information"),
    GENERALORIENTATION(2, "Vortrag Allgemeine StuOri"),
    YEARENDING(3, "Schuljahresendgespräch"),
    YEARPLANNING(4, "Schuljahresplanungsgespräch");
    companion object {
        fun getById(id: Int) = when (id) {
            1 -> LASTMINUTE
            2 -> GENERALORIENTATION
            3 -> YEARENDING
            4 -> YEARPLANNING
            else -> OTHER
        }
    }
}

@Serializable
data class KAoATypDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<KAoATypDto> = KAoATyp.values().map { KAoATypDto(it.id, it.desc) }
    }
}