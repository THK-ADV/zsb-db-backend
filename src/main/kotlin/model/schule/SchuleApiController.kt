package model.schule

import error_handling.HttpServerResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import model.kontakt.enum.KAoABetreuungDto
import model.kontakt.enum.TalentscoutDto
import model.schule.enum.KooperationspartnerDto
import model.schule.enum.SchulformDto
import utilty.*

fun Route.schoolsApi() = route("schools") {

    get {
        call.logRequest()
        val result = SchuleDao.getAll(call.parameters["resolve_ids"] == "true")
        val json = Json.encodeToJsonElement(result)
        call.respondJsonOk(json)
    }

    get("/cooperationPartner") {
        call.logRequest()
        val json = Json.encodeToJsonElement(KooperationspartnerDto.generate())
        call.respondJsonOk(json)
    }

    get("/kaoaSupervisors") {
        call.logRequest()
        val json = Json.encodeToJsonElement(KAoABetreuungDto.generate())
        call.respondJsonOk(json)
    }

    get("/talentScouts") {
        call.logRequest()
        val json = Json.encodeToJsonElement(TalentscoutDto.generate())
        call.respondJsonOk(json)
    }

    get("/schooltypes") {
        call.logRequest()
        val json = Json.encodeToJsonElement(SchulformDto.generate())
        call.respondJsonOk(json)
    }

    get("/{uuid}") {
        call.logRequest()
        val uuid = call.parseParamAsUUID("uuid") ?: return@get
        val schule = SchuleDao.getById(uuid)
        println(schule)
        val json = Json.encodeToJsonElement(schule)
        call.respondJsonOk(json)
    }

    post {
        call.logRequest()
        println("Schule")
        println(SchuleDto)
        val schuleDto = call.receive<SchuleDto>()
        println("schuleDto")
        println(schuleDto)
        if (call.checkId(schuleDto.id)) return@post
        val result = SchuleDao.createOrUpdate(schuleDto)
        call.respond(HttpServerResponse.map(result, HttpStatusCode.Created))
    }

    put {
        call.logRequest()
        val schuleDto = call.receive<SchuleDto>()
        if (call.checkIdAndRespondUsePostIfNull(schuleDto.id)) return@put
        val result = SchuleDao.createOrUpdate(schuleDto)
        call.respond(HttpServerResponse.map(result))
    }

    delete("/{uuid}") {
        call.logRequest()
        val uuid = call.parseParamAsUUID("uuid") ?: return@delete
        val isDeleted = SchuleDao.delete(uuid)
        if (isDeleted)
            call.respondTextAsJson("Successfully deleted $uuid")
        else
            call.respondTextAsJson("Couldn't find Schule with id: $uuid", status = HttpStatusCode.NotFound)
    }
}
