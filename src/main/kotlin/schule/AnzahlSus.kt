package schule

import kotlinx.serialization.Serializable

/**
 * PupilCount from few to many (A to I)
 */
enum class AnzahlSus(val id: Int, val range: String) {
    Z(0, "k. A."),
    A(1, "50-100"),
    B(2, "101-200"),
    C(3, "201-300"),
    D(4, "301-400"),
    E(5, "401-500"),
    F(6, "501-700"),
    G(7, "701-1000"),
    H(8, "1001-1500"),
    I(9, ">1501");

    companion object {
        fun getObjectByString(range: String) = when (range) {
            "50-100" -> A
            "101-200" -> B
            "201-300" -> C
            "301-400" -> D
            "401-500" -> E
            "501-700" -> F
            "701-1000" -> G
            "1001-1500" -> H
            ">1501" -> I
            else -> Z
        }
    }
}

@Serializable
data class AnzahlSusDto(val id: Int, val range: String) {
    companion object {
        fun generate(): List<AnzahlSusDto> {
            val list = mutableListOf<AnzahlSusDto>()
            AnzahlSus.values().forEach { list.add(AnzahlSusDto(it.id, it.range)) }
            return list
        }
    }
}