package model.communication

import MailSettings
import org.apache.commons.mail.EmailAttachment
import org.apache.commons.mail.MultiPartEmail
import java.io.File


class MailerService(val mailSettings: MailSettings) {

    fun sendMail(mail: MailDto, attachments: List<File>): Result<String> =
        kotlin.runCatching {
            val client: MultiPartEmail = MultiPartEmail().apply {
                hostName = mailSettings.host
                socketTimeout = mailSettings.timeout
                subject = mail.subject
                setFrom(mailSettings.sender)
                setMsg(mail.msg)
            }

            mail.addressees.chunked(mailSettings.chunkSize).forEach {
                it.forEach { recipient -> client.addBcc(recipient) }
            }

            // Anhänge hinzufügen
            attachments.forEach { file ->
                val attachment = EmailAttachment().apply {
                    path = file.absolutePath
                    disposition = EmailAttachment.ATTACHMENT
                }
                client.attach(attachment)
            }

            client.send()
        }
}

//zsb-db-backend
//docker exec -i zsb-db pg_dump -U postgres -d postgres -Fc > backup.dump
//scp backup.dump bertels@advm1.gm.fh-koeln.de:/home/bertels/zsb-backups/
//cronjob automatisieren