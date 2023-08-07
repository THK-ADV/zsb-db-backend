package model.address

import error_handling.HttpServerResponse
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import utilty.*

fun Route.adressenApi() {
    route("addresses") {
        get {
            call.logRequest()
            val result = AdresseDao.getAll(call.parameters["resolve_ids"] == "true")
            val json = Json.encodeToJsonElement(result)
            call.respondJsonOk(json)
        }

        get("/{id}") {
            call.logRequest()
            val adressId = call.parseParamAsUUID("id") ?: return@get
            val result = AdresseDao.getById(adressId, call.parameters["resolve_ids"] == "true")
            val json = Json.encodeToJsonElement(result)
            call.respondJsonOk(json)
        }

        put {
            call.logRequest()
            val adresseDto = call.receive<AdresseDto>()
            val result = AdresseDao.createOrUpdate(adresseDto)
            call.respond(HttpServerResponse.map(result))
        }
    }
}
