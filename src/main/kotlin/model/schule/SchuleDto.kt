package model.schule

import kotlinx.serialization.Serializable
import model.adresse.AdresseDto
import model.kontakt.KontaktDto
import model.ort.OrtDto


@Serializable
data class SchuleDto(
    val schule_id: String? = null,
    val name: String,
    val schulform: Int,
    val schwerpunkt: String?,
    val anzahl_sus: Int,
    val kooperationsvertrag: Boolean,
    val adress_id: String,
    val kontakte_ids: List<String> = listOf(),
    val kaoa_hochschule: Boolean,
    val talentscouting: Boolean,
    var kontakte: List<KontaktDto> = listOf(),
    val ort_id: Int? = null,
    var adresse: AdresseDto? = null,
    var ort: OrtDto? = null
)