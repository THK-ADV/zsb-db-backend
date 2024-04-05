package model.ort

import error_handling.HttpServerResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import model.address.AdresseDao
import model.address.AdresseDto
import utilty.*

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
            println("test")
            call.logRequest()
            val ortDto = call.receive<OrtDto>()
            println(ortDto)
            val result = OrtDao.createOrUpdate(ortDto)
            call.respond(HttpServerResponse.map(result))
        }

        post {
            call.logRequest()
            val ortDto = call.receive<OrtDto>()
            if (call.checkId(ortDto.id)) return@post
            val result = OrtDao.createOrUpdate(ortDto)
            call.respond(HttpServerResponse.map(result, HttpStatusCode.Created))
        }
    }
}
