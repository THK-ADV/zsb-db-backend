package schule

import error_handling.HttpServerResponse
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.*
import kotlinx.serialization.list
import utilty.*

fun Route.schuleApi() = route("schulen") {

    get {
        call.logRequest()
        val result = SchuleService.getAll()
        val json = Serializer.stable.toJson(SchuleDto.serializer().list, result)
        call.respondJsonOk(json)
    }

    get("/{id}") {
        call.logRequest()
        val id = call.getParameterAsIntOrNullAndRespondError("id") ?: return@get
        val schule = SchuleService.getById(id)
        val json = Serializer.stable.toJson(SchuleDto.serializer(), schule)
        call.respondJsonOk(json)
    }

    post {
        call.logRequest()
        val schuleDto = call.receive<SchuleDto>()
        if (call.checkIdAndRespondUsePutIfNotNull(schuleDto.schule_id)) return@post
        val result = SchuleService.createOrUpdate(schuleDto)
        call.respond(HttpServerResponse.map(result, HttpStatusCode.Created))
    }

    put {
        call.logRequest()
        val schuleDto = call.receive<SchuleDto>()
        if (call.checkIdAndRespondUsePostIfNull(schuleDto.schule_id)) return@put
        val result = SchuleService.createOrUpdate(schuleDto)
        call.respond(HttpServerResponse.map(result))
    }

    get("/schulform") {
        call.logRequest()
        val json = Serializer.stable.toJson(SchulformDto.serializer().list, SchulformDto.generate())
        call.respondJsonOk(json)
    }
}
