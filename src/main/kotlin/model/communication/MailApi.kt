package model.communication

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import utilty.logRequest

fun Route.mailApi() = route("email") {
    post {
        call.logRequest()
        val mail = MailerService()
        val content = call.receive<MailDto>()
        mail.sendMail(content)
        call.respond(HttpStatusCode.OK)
    }
}