package adresse

import kotlinx.serialization.Serializable

@Serializable
data class AdresseDto(
    val adress_id: Int? = null,
    val strasse: String,
    val hausnummer: String,
    val ort_id: Int
)
