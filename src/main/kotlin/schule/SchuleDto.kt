package schule

import adresse.AdresseDto
import kontakt.KontaktDto
import kotlinx.serialization.Serializable
import ort.OrtDto


@Serializable
data class SchuleDto(
    val schule_id: Int? = null,
    val name: String,
    val schulform: Int,
    val schwerpunkt: String?,
    val anzahl_sus: Int,
    val kooperationsvertrag: Boolean,
    val adress_id: Int,
    val kontakt_a_id: String?,
    val kontakt_b_id: String?,
    val stubo_kontakt_id: String?,
    val kaoa_hochschule: Boolean,
    val talentscouting: Boolean,
    var kontakt_a: KontaktDto? = null,
    var kontakt_b: KontaktDto? = null,
    var stubo_kontakt: KontaktDto? = null,
    val ort_id: Int? = null,
    var adresse: AdresseDto? = null,
    var ort: OrtDto? = null
)