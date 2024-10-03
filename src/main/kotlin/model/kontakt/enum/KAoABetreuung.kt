package model.kontakt.enum

import kotlinx.serialization.Serializable

enum class KAoABetreuung(val id: Int, val desc: String) {
    KEINE(0, "Keine"),
    SM(1, "Meusel, Sebastian"),
    FP(2, "Pavel, Florian"),
    GI(3, "Intveen, Geesche"),
    UP(4, "Pitsch, Ursula"),
    DL(5, "Lieske, Dominik"),
    SH(6, "Hopp, Sebastian"),
    TJ(7, "Jares, Thiemo"),
    LS(8, "Schröder, Lena"),
    FN(9, "Nieter, Franziska"),
    VP(10, "Pfeifer, Verena"),
    SR(11, "Ruetz, Stefanie"),
    GZ(12, "Zimmermann, Gillian"),
    KW(13, "Werner, Katharina"),
    MB(14, "Baron, Mari"),
    MG(15, "Grzonka, Moritz"),
    PL(16, "Labinski, Patricia");

    companion object {
        fun fromDesc(desc: String) = when (desc) {
            "Meusel, Sebastian" -> SM
            "Pavel, Florian" -> FP
            "Intveen, Geesche" -> GI
            "Pitsch, Ursula" -> UP
            "Lieske, Dominik" -> DL
            "Hopp, Sebastian" -> SH
            "Jares, Thiemo" -> TJ
            "Schröder, Lena" -> LS
            "Nieter, Franziska" -> FN
            "Pfeifer, Verena" -> VP
            "Ruetz, Stefanie" -> SR
            "Zimmermann, Gillian" -> GZ
            "Werner, Katharina" -> KW
            "Baron, Mari" -> MB
            "Grzonka, Moritz" -> MG
            "Labinski, Patricia" -> PL
            else -> KEINE
        }
    }
}

@Serializable
data class KAoABetreuungDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<KAoABetreuungDto> = KAoABetreuung.values()
            .map { KAoABetreuungDto(it.id, it.desc) }
    }
}