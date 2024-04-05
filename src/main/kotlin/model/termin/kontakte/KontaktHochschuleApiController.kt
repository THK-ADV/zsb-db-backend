package model.termin.kontakte

import error_handling.HttpServerResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import utilty.*

fun Route.kontakteHochschuleApi() {
    route("universityContacts") {
        get {
            call.logRequest()
            val result = KontaktHochschuleDao.getAll()
            val json = Json.encodeToJsonElement(result)
            call.respondJsonOk(json)
        }

        post {
            call.logRequest()
            val kontaktHochschuleDto = call.receive<KontaktHochschuleDto>()
            if (call.checkId(kontaktHochschuleDto.id)) return@post
            val result = KontaktHochschuleDao.create(kontaktHochschuleDto)
            call.respond(HttpServerResponse.map(result, HttpStatusCode.Created))
        }
    }
}