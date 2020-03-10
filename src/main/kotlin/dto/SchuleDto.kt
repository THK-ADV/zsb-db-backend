package dto

import kotlinx.serialization.Serializable

@Serializable
data class SchuleDto(
    val schule_id: Int?,
    val schulform: String,
    val schwerpunkt: String,
    val kooperationsvertrag: Boolean,
    val adresse: AdresseDto,
    val schulleitung_mail: String,
    val stubo_mail: String,
    val schueleranzahl: Int,
    val kaoa_hochschule: Boolean,
    val talentscouting: Boolean
)

@Serializable
data class AdresseDto(
    val adress_id: Int?,
    val strasse: String,
    val hausnummer: String,
    val ort: OrtDto
)

@Serializable
data class OrtDto(
    val ort_id: Int?,
    val plz: Int,
    val bezeichnung: String
)
