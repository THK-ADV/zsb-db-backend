package adresse

import kotlinx.serialization.Serializable
import ort.OrtDto

@Serializable
data class AdresseDto(
    val adress_id: String? = null,
    val strasse: String,
    val hausnummer: String,
    val ort_id: String,
    var ort: OrtDto? = null
)
