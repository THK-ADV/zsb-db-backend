package model.communication

import org.apache.commons.mail.Email
import org.apache.commons.mail.SimpleEmail

class MailerService {

    fun sendMail(mail: MailDto) {
        val client: Email = SimpleEmail()
        client.hostName = "mail.gm.fh-koeln.de"
        client.setFrom(mail.sender)
        client.setSubject(mail.subject)
        client.setMsg(mail.message)
        for(i in mail.recipients)
            client.addTo(i)
        client.send()
    }

}