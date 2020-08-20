package model.ort

import error_handling.HttpServerResponse
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.put
import io.ktor.routing.route
import kotlinx.serialization.list
import utilty.*

fun Route.orteApi() {
   route("orte") {
        get {
            call.logRequest()
            val result = OrtDao.getAll()
            val json = Serializer.stable.toJson(OrtDto.serializer().list, result)
            call.respondJsonOk(json)
        }

        get("/{id}") {
            call.logRequest()
            val uuid = call.getParameterAsUuidOrNullAndRespondError("uuid") ?: return@get
            val ort = OrtDao.getById(uuid)
            val json = Serializer.stable.toJson(OrtDto.serializer(), ort)
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