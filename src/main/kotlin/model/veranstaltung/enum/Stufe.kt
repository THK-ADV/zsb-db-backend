package model.veranstaltung.enum

enum class Stufe(val id: Int, val desc: String) {
    STUFE_1(1, "Stufe 1"),
    UNKNOWN(9, "Unbekannt");

    companion object {
        fun getStufeByDesc(desc: String) = when (desc) {
            "Stufe 1" -> STUFE_1
            else -> UNKNOWN
        }
    }
}

data class StufeDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<StufeDto> = Stufe.values().map { StufeDto(it.id, it.desc)  }
    }
}