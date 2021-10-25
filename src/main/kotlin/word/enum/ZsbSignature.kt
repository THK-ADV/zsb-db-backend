package word.enum

import kotlinx.serialization.Serializable


enum class ZsbSignatur(val id: Int, val desc: String, val text: String) {
    NONE(0, "Keine", ""),
    ZSB(1,"ZSB","Technische Hochschule Köln\nZentrale Studienberatung\nClaudiusstraße 1\n50859 Köln\nM: studienberatung@th-koeln.de\nT: 0221/8275-5500");
}

@Serializable
data class ZsbSignaturDto(val id: Int, val desc: String, val text: String) {
    companion object {
        fun generate(): List<ZsbSignaturDto> =
            ZsbSignatur.values().map { ZsbSignaturDto(it.id, it.desc, it.text) }
    }
}
