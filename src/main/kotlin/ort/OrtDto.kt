package ort

import kotlinx.serialization.Serializable


@Serializable
data class OrtDto(
    val ort_id: Int? = null,
    val plz: Int,
    val bezeichnung: String
)
