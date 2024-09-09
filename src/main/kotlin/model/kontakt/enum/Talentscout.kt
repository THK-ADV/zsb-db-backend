package model.kontakt.enum

import kotlinx.serialization.Serializable

enum class Talentscout(val id: Int, val desc: String) {
    KEINE(0, "Keine"),
    FN(1, "Nieter, Franziska"),
    SH(2, "Hopp, Sebastian"),
    TJ(3, "Jares, Thiemo"),
    LS(4, "Schröder, Lena"),
    GZ(5, "Zimmermann, Gillian"),
    VP(6, "Pfeifer, Verena"),
    SR(7, "Ruetz, Stefanie"),
    KW(8, "Werner, Katharina"),
    MG(9, "Grzonka, Moritz"),
    MH(10, "Hamoud, Marwa"),
    SB(11, "Berhane, Sara");

    companion object {
        fun fromDesc(desc: String) = when (desc) {
            "Nieter, Franziska" -> FN
            "Hopp, Sebastian" -> SH
            "Jares, Thiemo" -> TJ
            "Schröder, Lena" -> LS
            "Zimmermann, Gillian" -> GZ
            "Pfeifer, Verena" -> VP
            "Ruetz, Stefanie" -> SR
            "Werner, Katharina" -> KW
            "Grzonka, Moritz" -> MG
            "Hamoud, Marwa" -> MH
            "Berhane, Sara" -> SB
            else -> KEINE
        }
    }
}

@Serializable
data class TalentscoutDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<TalentscoutDto> = Talentscout.values()
            .map { TalentscoutDto(it.id, it.desc) }
    }
}