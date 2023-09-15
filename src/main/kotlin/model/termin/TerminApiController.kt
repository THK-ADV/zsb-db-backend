package model.termin

import error_handling.HttpServerResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import model.termin.enum.KategorieDto
import model.termin.enum.StufeDto
import model.termin.enum.VortragsartDto
import utilty.*

fun Route.termineApi() {
    route("events") {

        // get Kategorie options
        get("/categories") {
            call.logRequest()
            val json = Json.encodeToJsonElement(KategorieDto.generate())
            call.respondJsonOk(json)
        }

        // get stufe options
        get("/levels") {
            call.logRequest()
            val json = Json.encodeToJsonElement(StufeDto.generate())
            call.respondJsonOk(json)
        }

        // get vortragsart options
        get("/presentationtypes") {
            call.logRequest()
            val json = Json.encodeToJsonElement(VortragsartDto.generate())
            call.respondJsonOk(json)
        }

        // get all
        get {
            call.logRequest()
            val result = TerminDao.getAll()
            call.respond(HttpServerResponse.map(result))
        }

        // get one by id
        get("/{uuid}") {
            call.logRequest()
            val uuid = call.parseParamAsUUID("uuid") ?: return@get
            val result = TerminDao.getById(uuid, call.parameters["resolve_ids"] == "true")
            call.respond(HttpServerResponse.map(result))
        }

        // create
        post { postOrPut(call, isPost = true) }

        // update
        put { postOrPut(call) }

        // delete
        delete("/{uuid}") {
            call.logRequest()
            val uuid = call.parseParamAsUUID("uuid") ?: return@delete
            val isDeleted = TerminDao.delete(uuid)
            if (isDeleted)
                call.respondTextAsJson("Successfully deleted $uuid")
            else
                call.respondTextAsJson("Couldn't find Termin with id: $uuid", status = HttpStatusCode.NotFound)
        }
    }
}

private suspend fun postOrPut(call: ApplicationCall, isPost: Boolean = false) {
    call.logRequest()
    val terminDto = call.receive<TerminDto>()

    if (isPost) {
        if (call.checkId(terminDto.uuid)) return
    } else
        if (call.checkIdAndRespondUsePostIfNull(terminDto.uuid)) return

    val result = TerminDao.createOrUpdate(terminDto)
    call.respond(HttpServerResponse.map(result))
}
