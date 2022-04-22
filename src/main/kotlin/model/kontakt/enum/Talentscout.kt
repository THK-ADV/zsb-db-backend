package model.kontakt.enum

enum class Talentscout(val id: Int, val desc: String) {
    KEINE(0, "Keine"),
    AA(1, "Abdirahman, Amal"),
    SH(2, "Hopp, Sebastian"),
    TY(3, "Yares, Thiemo"),
    LS(4, "Schröder, Lena"),
    SY(5, "Yilmaz-Dreger, Serap"),
    VP(6, "Pfeifer, Verena"),
    SR(7, "Ruetz, Stefanie");

    companion object {
        fun fromDesc(desc: String) = when (desc) {
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