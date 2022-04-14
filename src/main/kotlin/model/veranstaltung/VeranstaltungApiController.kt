package model.veranstaltung

import error_handling.HttpServerResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import model.veranstaltung.enum.KategorieDto
import model.veranstaltung.enum.StufeDto
import model.veranstaltung.enum.VortragsartDto
import utilty.*

fun Route.veranstaltungenApi() {
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
            val result = VeranstaltungDao.getAll(call.parameters["resolve_ids"] == "true")
            call.respond(HttpServerResponse.map(result))
        }

        // get one by id
        get("/{uuid}") {
            call.logRequest()
            val uuid = call.getParameterAsUuidOrNullAndRespondError("uuid") ?: return@get
            val result = VeranstaltungDao.getById(uuid, call.parameters["resolve_ids"] == "true")
            call.respond(HttpServerResponse.map(result))
        }

        // create
        post { postOrPut(call, isPost = true) }

        // update
        put { postOrPut(call) }

        // delete
        delete("/{uuid}") {
            call.logRequest()
            val uuid = call.getParameterAsUuidOrNullAndRespondError("uuid") ?: return@delete
            val isDeleted = VeranstaltungDao.delete(uuid)
            if (isDeleted)
                call.respondTextAsJson("Successfully deleted $uuid")
            else
                call.respondTextAsJson("Couldn't find Veranstaltung with id: $uuid", status = HttpStatusCode.NotFound)
        }
    }
}

private suspend fun postOrPut(call: ApplicationCall, isPost: Boolean = false) {
    call.logRequest()
    val veranstaltungDto = call.receive<VeranstaltungDto>()

    if (isPost) {
        if (call.checkIdAndRespondUsePutIfNotNull(veranstaltungDto.uuid)) return
    } else
        if (call.checkIdAndRespondUsePostIfNull(veranstaltungDto.uuid)) return

    val result = VeranstaltungDao.createOrUpdate(veranstaltungDto)
    call.respond(HttpServerResponse.map(result))
}
