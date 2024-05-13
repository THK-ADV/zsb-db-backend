package model.communication

import kotlinx.serialization.Serializable
import model.schule.SchuleDto

@Serializable
data class MailDto(
    val msg: String,
    var addressees: List<String>,
    var schools: List<SchuleDto>,
    val subject: String
)