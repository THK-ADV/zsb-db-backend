package adresse

import error_handling.HttpServerResponse
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import utilty.*

fun Route.adresseApi() {
    val serializer = Json(JsonConfiguration.Stable)

    route("adressen") {
        get {
            call.logRequest()
            val result = AdresseService.getAll()
            val json = serializer.toJson(AdresseDto.serializer().list, result)
            call.respondJsonOk(json)
        }

        get("/{id}") {
            call.logRequest()
            val adressId = call.getParameterAsIntOrNullAndRespondError("id") ?: return@get
            val result = AdresseService.getById(adressId)
            val json = serializer.toJson(AdresseDto.serializer(), result)
            call.respondJsonOk(json)
        }

        post {
            call.logRequest()
            val adresseDto = call.receive<AdresseDto>()
            if (call.checkIdAndRespondUsePutIfNotNull(adresseDto.adress_id)) return@post
            val result = AdresseService.createOrUpdate(adresseDto)
            call.respond(HttpServerResponse.map(result, HttpStatusCode.Created))
        }

        put {
            call.logRequest()
            val adresseDto = call.receive<AdresseDto>()
            if (call.checkIdAndRespondUsePostIfNull(adresseDto.adress_id)) return@put
            val result = AdresseService.createOrUpdate(adresseDto)
            call.respond(HttpServerResponse.map(result))
        }


    }
}