package model.address

import error_handling.HttpServerResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import model.schule.SchuleDao
import model.schule.SchuleDto
import utilty.*

fun Route.adressenApi() {
    route("addresses") {
        get {
            call.logRequest()
            val result = AdresseDao.getAll()
            val json = Json.encodeToJsonElement(result)
            call.respondJsonOk(json)
        }

        get("/{id}") {
            call.logRequest()
            val addressId = call.parseParamAsUUID("id") ?: return@get
            val result = AdresseDao.getById(addressId)
            val json = Json.encodeToJsonElement(result)
            call.respondJsonOk(json)
        }

        put {
            call.logRequest()
            val addressDto = call.receive<AdresseDto>()
            println(addressDto)
            val result = AdresseDao.createOrUpdate(addressDto)
            call.respond(HttpServerResponse.map(result))
        }

        post {
            call.logRequest()
            val addressDto = call.receive<AdresseDto>()
            if (call.checkId(addressDto.id)) return@post
            val result = AdresseDao.createOrUpdate(addressDto)
            call.respond(HttpServerResponse.map(result, HttpStatusCode.Created))
        }
    }
}
