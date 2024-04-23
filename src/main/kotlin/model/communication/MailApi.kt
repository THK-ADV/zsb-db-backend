package model.communication

import MailSettings
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import utilty.logRequest

fun Route.mailApi(mailSettings: MailSettings) = route("email") {
    val mail = MailerService(mailSettings)

    post {
        call.logRequest()
        val content = call.receive<MailDto>()
        val result = mail.sendMail(content).getOrThrow()
        call.respond(HttpStatusCode.OK)
    }
}
