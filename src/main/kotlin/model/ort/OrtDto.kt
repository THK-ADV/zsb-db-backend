package model.ort

import kotlinx.serialization.Serializable


@Serializable
data class OrtDto(
    val ort_id: String? = null,
    val plz: Int,
    val bezeichnung: String,
    val kreis: String,
    val regierungsbezirk: String
)