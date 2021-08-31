package model.address

import error_handling.HttpServerResponse
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.put
import io.ktor.routing.route
import kotlinx.serialization.list
import utilty.*

fun Route.adressenApi() {
    route("adressen") {
        get {
            call.logRequest()
            val result = AdresseDao.getAll(call.parameters["resolve_ids"] == "true")
            val json = Serializer.stable.toJson(AdresseDto.serializer().list, result)
            call.respondJsonOk(json)
        }

        get("/{id}") {
            call.logRequest()
            val adressId = call.getParameterAsUuidOrNullAndRespondError("id") ?: return@get
            val result = AdresseDao.getById(adressId, call.parameters["resolve_ids"] == "true")
            val json = Serializer.stable.toJson(AdresseDto.serializer(), result)
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