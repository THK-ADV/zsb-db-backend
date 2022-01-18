package model.communication

import kotlinx.serialization.Serializable

@Serializable
data class MailDto(
    val msg: String,
    var addressees: List<String>,
    val subject: String
)