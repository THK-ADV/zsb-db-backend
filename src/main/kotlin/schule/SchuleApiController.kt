package schule

import error_handling.HttpServerResponse
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.*
import kotlinx.serialization.list
import utilty.*

fun Route.schuleApi() {
    route("schulen") {

        get {
            call.logRequest()
            val resolveIds = call.parameters["resolve_ids"]
            val result = if (resolveIds == "true") {
                SchuleDao.getAllAtomic()
            } else {
                SchuleDao.getAll()
            }

            val json = Serializer.stable.toJson(SchuleDto.serializer().list, result)
            call.respondJsonOk(json)
        }

        get("/{id}") {
            call.logRequest()
            val id = call.getParameterAsIntOrNullAndRespondError("id") ?: return@get

            val resolveIds = call.parameters["resolve_ids"]
            val schule = if (resolveIds == "true") {
                SchuleDao.getByIdAtomic(id)
            } else {
                SchuleDao.getById(id)
            }

            val json = Serializer.stable.toJson(SchuleDto.serializer(), schule)
            call.respondJsonOk(json)
        }

        post {
            call.logRequest()
            val schuleDto = call.receive<SchuleDto>()
            if (call.checkIdAndRespondUsePutIfNotNull(schuleDto.schule_id)) return@post
            val result = SchuleDao.createOrUpdate(schuleDto)
            call.respond(HttpServerResponse.map(result, HttpStatusCode.Created))
        }

        put {
            call.logRequest()
            val schuleDto = call.receive<SchuleDto>()
            if (call.checkIdAndRespondUsePostIfNull(schuleDto.schule_id)) return@put
            val result = SchuleDao.createOrUpdate(schuleDto)
            call.respond(HttpServerResponse.map(result))
        }
    }
}