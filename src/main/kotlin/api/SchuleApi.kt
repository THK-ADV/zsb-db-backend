package api

import dto.SchuleDto
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import service.SchuleService

fun Route.schulenApi() {
    val serializer = Json(JsonConfiguration.Stable)

    route("schulen") {

        get {
            call.logRequest()
            val result = SchuleService.getAll()
            val json = serializer.toJson(SchuleDto.serializer().list, result)
            call.respondJsonOk(json)
        }

        get("/{id}") {
            call.logRequest()
            val id = call.getParameterAsIntOrNullAndRespondError("id") ?: return@get
            val schule = SchuleService.getById(id)
            val json = serializer.toJson(SchuleDto.serializer(), schule)
            call.respondJsonOk(json)
        }

        put {
            call.logRequest()
            val schuleDto = call.receive<SchuleDto>()
            if (schuleDto.schule_id == null) {
                call.respondText("ID not given. Use post to create.", ContentType.Text.Plain, HttpStatusCode.BadRequest)
                return@put
            }
            val result = SchuleService.createOrUpdate(schuleDto)
            call.respond(HttpServerResponse.map(result))
        }

        post {
            call.logRequest()
            val schuleDto = call.receive<SchuleDto>()
            if (schuleDto.schule_id != null) {
                call.respondText("ID given. Use put to update instead.", ContentType.Text.Plain, HttpStatusCode.BadRequest)
                return@post
            }
            val result = SchuleService.createOrUpdate(schuleDto)
            call.respond(HttpServerResponse.map(result, HttpStatusCode.Created))
        }
    }
}