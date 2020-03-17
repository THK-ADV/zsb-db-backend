package schule

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
