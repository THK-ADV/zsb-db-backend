package model.communication

import MailSettings
import org.apache.commons.mail.Email
import org.apache.commons.mail.SimpleEmail

class MailerService(val mailSettings: MailSettings) {

    private val client: Email = SimpleEmail().apply {
        hostName = mailSettings.host
        socketTimeout = mailSettings.timeout
        setFrom(mailSettings.sender)
        setDebug(true)
    }

    fun sendMail(mail: MailDto): Result<String> =
        kotlin.runCatching {
            client.setSubject(mail.subject)
            client.setMsg(mail.msg)
            mail.addressees.chunked(mailSettings.chunkSize) {
                for (i in it) client.addTo(i)
            }
            client.send()
        }
}