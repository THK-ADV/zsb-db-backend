package model.communication

import kotlinx.serialization.Serializable

@Serializable
data class MailDto(
    val msg: String,
    val addressees: List<String>,
    val subject: String,
    val sender: String,
    val attachements: List<ByteArray>
)