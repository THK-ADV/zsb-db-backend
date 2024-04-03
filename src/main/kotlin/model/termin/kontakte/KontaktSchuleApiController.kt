package model.termin.kontakte

import error_handling.HttpServerResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import utilty.*

fun Route.kontakteSchuleApi() {
    route("schoolContacts") {
        get {
            call.logRequest()
            val result = KontaktSchuleDao.getAll()
            val json = Json.encodeToJsonElement(result)
            call.respondJsonOk(json)
        }

        post {
            call.logRequest()
            val kontaktSchuleDto = call.receive<KontaktSchuleDto>()
            if (call.checkId(kontaktSchuleDto.id)) return@post
            val result = KontaktSchuleDao.create(kontaktSchuleDto)
            call.respond(HttpServerResponse.map(result, HttpStatusCode.Created))
        }
    }
}