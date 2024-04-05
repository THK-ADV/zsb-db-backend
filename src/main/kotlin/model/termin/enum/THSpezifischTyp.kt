package model.termin.enum

import kotlinx.serialization.Serializable

enum class THSpezifischTyp(val id: Int, val desc: String) {
    OTHER(0, "Sonstiges"),
    SINGLEAPPT(1,  "Einzeltermin"),
    CONSULTATIONSINGLE(2, "Beratung Einzeltermin"),
    TALKSINGLE(3, "Fachvortrag Einzeltermin"),
    THTALKSINGLE(4, "Vortrag 'Technology, Arts, Sciences' Einzeltermin"),
    INFORMATIONSINGLE(5, "Informationsstand Einzeltermin"),
    OTHERSINGLE(6, "Sonstiges Einzeltermin"),
    SCHOOLFAIR(7,  "Schulmesse"),
    CONSULTATIONFAIR(8, "Beratung Schulmesse"),
    TALKFAIR(9, "Fachvortrag Schulmesse"),
    THTALKFAIR(10, "Vortrag 'Technology, Arts, Sciences' Schulmesse"),
    INFORMATIONFAIR(11, "Informationsstand Schulmesse"),
    OTHERFAIR(12, "Sonstiges Schulmesse");
    companion object {
        fun getById(id: Int) = when (id) {
            1 -> SINGLEAPPT
            2 -> CONSULTATIONSINGLE
            3 -> TALKSINGLE
            4 -> THTALKSINGLE
            5 -> INFORMATIONSINGLE
            6 -> OTHERSINGLE
            7 -> SCHOOLFAIR
            8 -> CONSULTATIONFAIR
            9 -> TALKFAIR
            10 -> THTALKFAIR
            11 -> INFORMATIONFAIR
            12 -> OTHERFAIR
            else -> OTHER
        }

        fun getByDesc(desc: String) = when(desc) {
            "Einzeltermin" -> SINGLEAPPT
            "Beratung Einzeltermin" -> CONSULTATIONSINGLE
            "Fachvortrag Einzeltermin" -> TALKSINGLE
            "Vortrag 'Technology, Arts, Sciences' Einzeltermin" -> THTALKSINGLE
            "Informationsstand Einzeltermin" -> INFORMATIONSINGLE
            "Sonstiges Einzeltermin" -> OTHERSINGLE
            "Schulmesse" -> SCHOOLFAIR
            "Beratung Schulmesse" -> CONSULTATIONFAIR
            "Fachvortrag Schulmesse" -> TALKFAIR
            "Vortrag 'Technology, Arts, Sciences' Schulmesse" -> THTALKFAIR
            "Informationsstand Schulmesse" -> INFORMATIONFAIR
            "Sonstiges Schulmesse" -> OTHERFAIR
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