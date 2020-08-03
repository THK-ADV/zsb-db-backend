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
        val resolveIds = call.parameters["resolve_ids"]
        val result = if (resolveIds == "true") {
            SchuleDao.getAllAtomic()
        } else {
            SchuleDao.getAll()
        }

        val json = Serializer.stable.toJson(SchuleDto.serializer().list, result)
        call.respondJsonOk(json)
    }

    get("/schulformen") {
        call.logRequest()
        val json = Serializer.stable.toJson(SchulformDto.serializer().list, SchulformDto.generate())
        call.respondJsonOk(json)
    }

    get("/anzahl_sus") {
        call.logRequest()
        val json = Serializer.stable.toJson(AnzahlSusDto.serializer().list, AnzahlSusDto.generate())
        call.respondJsonOk(json)
    }

    get("/{uuid}") {
        call.logRequest()
        val uuid = call.getParameterAsUuidOrNullAndRespondError("uuid") ?: return@get

        val resolveIds = call.parameters["resolve_ids"]
        val schule = if (resolveIds == "true") {
            SchuleDao.getByIdAtomic(uuid)
        } else {
            SchuleDao.getById(uuid)
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
//        val test = call.receive<String>()
//        log.debug(test)
//        log.debug("THE END")
//        call.respondText { "respond" }
        val schuleDto = call.receive<SchuleDto>()
        if (call.checkIdAndRespondUsePostIfNull(schuleDto.schule_id)) return@put
        val result = SchuleDao.createOrUpdate(schuleDto)
        call.respond(HttpServerResponse.map(result))
    }
}
