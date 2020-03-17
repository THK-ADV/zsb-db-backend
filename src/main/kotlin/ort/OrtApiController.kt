package ort

import error_handling.HttpServerResponse
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.*
import kotlinx.serialization.list
import utilty.*

fun Route.ortApi() {
   route("orte") {
        get {
            call.logRequest()
            val result = OrtService.getAll()
            val json = Serializer.stable.toJson(OrtDto.serializer().list, result)
            call.respondJsonOk(json)
        }

        get("/{id}") {
            call.logRequest()
            val id = call.getParameterAsIntOrNullAndRespondError("id") ?: return@get
            val ort = OrtService.getById(id)
            val json = Serializer.stable.toJson(OrtDto.serializer(), ort)
            call.respondJsonOk(json)
        }

        post {
            call.logRequest()
            val ortDto = call.receive<OrtDto>()
            if (call.checkIdAndRespondUsePutIfNotNull(ortDto.ort_id)) return@post
            val result = OrtService.createOrUpdate(ortDto)
            call.respond(HttpServerResponse.map(result))
        }

        put {
            call.logRequest()
            val ortDto = call.receive<OrtDto>()
            if (call.checkIdAndRespondUsePostIfNull(ortDto.ort_id)) return@put
            val result = OrtService.createOrUpdate(ortDto)
            call.respond(HttpServerResponse.map(result))
        }
    }
}