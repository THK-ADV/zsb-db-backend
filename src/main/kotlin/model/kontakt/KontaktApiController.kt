package model.kontakt

import error_handling.HttpServerResponse
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import model.kontakt.enum.AnredeDto
import model.kontakt.enum.KontaktFunktionDto
import utilty.*

fun Route.kontakteApi() {
    route("contacts") {
        get {
            call.logRequest()
            val result = KontaktDao.getAll()
            val json = Json.encodeToJsonElement(result)
            call.respondJsonOk(json)
        }

        get("/feature") {
            call.logRequest()
            val json = Json.encodeToJsonElement(KontaktFunktionDto.generate())
            call.respondJsonOk(json)
        }

        get("/salutations") {
            call.logRequest()
            val json = Json.encodeToJsonElement(AnredeDto.generate())
            call.respondJsonOk(json)
        }

        get("/{uuid}") {
            call.logRequest()
            val uuid = call.getParameterAsUuidOrNullAndRespondError("uuid") ?: return@get

            val kontakt = KontaktDao.getById(uuid)
            val json = Json.encodeToJsonElement(kontakt)
            call.respondJsonOk(json)
        }

        put {
            call.logRequest()
            val kontaktDto = call.receive<KontaktDto>()
            val result = KontaktDao.createOrUpdate(kontaktDto)
            call.respond(HttpServerResponse.map(result))
        }
    }
}
