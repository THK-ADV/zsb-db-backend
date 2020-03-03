package api

import dto.SchuleDto
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
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

    }
}