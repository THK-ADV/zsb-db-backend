package kontakt

import kotlinx.serialization.Serializable

@Serializable
data class KontaktDto(
    val uuid: String?,
    val name: String,
    val vorname: String = "",
    val anrede: Int? = null,
    val email: String,
    val funktion: Int? = null
)
