package ort

import error_handling.HttpServerResponse
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.put
import io.ktor.routing.route
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

        put {
            call.logRequest()
//            val jsonText = call.receive<String>()
//            ColoredLogging.LOG.debug(jsonText)
//            call.respondText { "everything is gonna be ok" }

            val ortDto = call.receive<OrtDto>()
            val result = OrtService.createOrUpdate(ortDto)
            call.respond(HttpServerResponse.map(result))
        }
    }
}