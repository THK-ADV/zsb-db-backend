package adresse

import error_handling.HttpServerResponse
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.*
import kotlinx.serialization.list
import utilty.*

fun Route.adresseApi() {
    route("adressen") {
        get {
            call.logRequest()

            val resolveIds = call.parameters["resolve_ids"]
            val result = if (resolveIds == "true") {
                AdresseDao.getAllAtomic()
            } else {
                AdresseDao.getAll()
            }

            val json = Serializer.stable.toJson(AdresseDto.serializer().list, result)
            call.respondJsonOk(json)
        }

        get("/{id}") {
            call.logRequest()
            val adressId = call.getParameterAsIntOrNullAndRespondError("id") ?: return@get
            val result = AdresseDao.getById(adressId)
            val json = Serializer.stable.toJson(AdresseDto.serializer(), result)
            call.respondJsonOk(json)
        }

        post {
            call.logRequest()
            val adresseDto = call.receive<AdresseDto>()
            if (call.checkIdAndRespondUsePutIfNotNull(adresseDto.adress_id)) return@post
            val result = AdresseDao.createOrUpdate(adresseDto)
            call.respond(HttpServerResponse.map(result, HttpStatusCode.Created))
        }

        put {
            call.logRequest()
            val adresseDto = call.receive<AdresseDto>()
            if (call.checkIdAndRespondUsePostIfNull(adresseDto.adress_id)) return@put
            val result = AdresseDao.createOrUpdate(adresseDto)
            call.respond(HttpServerResponse.map(result))
        }
    }
}