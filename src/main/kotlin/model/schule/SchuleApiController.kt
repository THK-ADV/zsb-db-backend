package model.schule

import error_handling.HttpServerResponse
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.*
import kotlinx.serialization.list
import model.schule.enum.AnzahlSusDto
import model.schule.enum.KooperationspartnerDto
import model.schule.enum.SchulformDto
import utilty.*

fun Route.schulenApi() = route("schulen") {

    get {
        call.logRequest()
        val result = SchuleDao.getAll(call.parameters["resolve_ids"] == "true")
        val json = Serializer.stable.toJson(SchuleDto.serializer().list, result)
        call.respondJsonOk(json)
    }

    get("/kooperationspartner") {
        call.logRequest()
        val json = Serializer.stable.toJson(KooperationspartnerDto.serializer().list, KooperationspartnerDto.generate())
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
        val schule = SchuleDao.getById(uuid, call.parameters["resolve_ids"] == "true")
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

    delete("/{uuid}") {
        call.logRequest()
        val uuid = call.getParameterAsUuidOrNullAndRespondError("uuid") ?: return@delete
        val isDeleted = SchuleDao.delete(uuid)
        if (isDeleted)
            call.respondTextAsJson("Successfully deleted $uuid")
        else
            call.respondTextAsJson("Couldn't find Schule with id: $uuid", status = HttpStatusCode.NotFound)
    }
}
