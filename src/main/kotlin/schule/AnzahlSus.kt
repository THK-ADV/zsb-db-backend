package schule

import kotlinx.serialization.Serializable

/**
 * PupilCount from few to many (A to I)
 */
enum class AnzahlSus(val range: String) {
    A("50-100"),
    B("101-200"),
    C("201-300"),
    D("301-400"),
    E("401-500"),
    F("501-700"),
    G("701-1000"),
    H("1001-1500"),
    I(">1501"),
}

@Serializable
data class AnzahlSusDto(val range: String) {
    companion object {
        fun generate(): List<AnzahlSusDto> {
            val list = mutableListOf<AnzahlSusDto>()
            AnzahlSus.values().forEach { list.add(AnzahlSusDto(it.range)) }
            return list
        }
    }
}