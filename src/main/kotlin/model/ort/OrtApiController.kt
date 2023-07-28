package model.ort

import error_handling.HttpServerResponse
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import utilty.parseParamAsUUID
import utilty.logRequest
import utilty.respond
import utilty.respondJsonOk

fun Route.orteApi() {
    route("cities") {
        get {
            call.logRequest()
            val result = OrtDao.getAll()
            val json = Json.encodeToJsonElement(result)
            call.respondJsonOk(json)
        }

        get("/{id}") {
            call.logRequest()
            val uuid = call.parseParamAsUUID("uuid") ?: return@get
            val ort = OrtDao.getById(uuid)
            val json = Json.encodeToJsonElement(ort)
            call.respondJsonOk(json)
        }

        put {
            call.logRequest()
            val ortDto = call.receive<OrtDto>()
            val result = OrtDao.createOrUpdate(ortDto)
            call.respond(HttpServerResponse.map(result))
        }
    }
}
