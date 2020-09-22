package word

import kotlinx.serialization.Serializable
import model.schule.SchuleDto

@Serializable
data class SerialLetterDto(
    val msg: String,
    val addressees: List<SchuleDto>,
    val signature_id: Int
)

