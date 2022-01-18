package model.communication

import MailSettings
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import utilty.logRequest

fun Route.mailApi(mailSettings: MailSettings) = route("email") {
    val mail = MailerService(mailSettings)

    post {
        call.logRequest()
        val content = call.receive<MailDto>()
        mail.sendMail(content)
        call.respond(HttpStatusCode.OK)
    }
}