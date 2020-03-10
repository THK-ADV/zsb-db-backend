package dto

import kotlinx.serialization.Serializable

@Serializable
data class SchuleDto(
    val schule_id: Int? = null,
    val schulform: String,
    val schwerpunkt: String,
    val kooperationsvertrag: Boolean,
    val adress_id: Int,
    val schulleitung_mail: String,
    val stubo_mail: String,
    val schueleranzahl: Int,
    val kaoa_hochschule: Boolean,
    val talentscouting: Boolean
)

@Serializable
data class AdresseDto(
    val adress_id: Int? = null,
    val strasse: String,
    val hausnummer: String,
    val ort_id: Int
)

@Serializable
data class OrtDto(
    val ort_id: Int? = null,
    val plz: Int,
    val bezeichnung: String
)
