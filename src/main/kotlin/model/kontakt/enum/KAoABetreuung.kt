package model.kontakt.enum

import kotlinx.serialization.Serializable

enum class KAoABetreuung(val id: Int, val desc: String) {
    KEINE(0, "Keine"),
    SM(1, "Meusel, Sebastian"),
    FP(2, "Pavel, Florian"),
    GI(3, "Intveen, Geesche"),
    DS(4, "Schulz, Denise"),
    AA(5, "Abdirahman, Amal"),
    SH(6, "Hopp, Sebastian"),
    TY(7, "Yares, Thiemo"),
    LS(8, "Schröder, Lena"),
    SY(9, "Yilmaz-Dreger, Serap"),
    VP(10, "Pfeifer, Verena"),
    SR(11, "Ruetz, Stefanie");

    companion object {
        fun fromDesc(desc: String) = when (desc) {
            "Meusel, Sebastian" -> SM
            "Pavel, Florian" -> FP
            "Intveen, Geesche" -> GI
            "Schulz, Denise" -> DS
            "Abdirahman, Amal" -> AA
            "Hopp, Sebastian" -> SH
            "Yares, Thiemo" -> TY
            "Schröder, Lena" -> LS
            "Yilmaz-Dreger, Serap" -> SY
            "Pfeifer, Verena" -> VP
            "Ruetz, Stefanie" -> SR
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