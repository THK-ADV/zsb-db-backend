package model.veranstaltung

import error_handling.HttpServerResponse
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.*
import kotlinx.serialization.list
import model.veranstaltung.enum.KategorieDto
import model.veranstaltung.enum.StufeDto
import model.veranstaltung.enum.VortragsartDto
import utilty.*

fun Route.veranstaltungenApi() {
    route("events") {

        // get Kategorie options
        get("/categories") {
            call.logRequest()
            val json = Serializer.stable.toJson(KategorieDto.serializer().list, KategorieDto.generate())
            call.respondJsonOk(json)
        }

        // get stufe options
        get("/levels") {
            call.logRequest()
            val json = Serializer.stable.toJson(StufeDto.serializer().list, StufeDto.generate())
            call.respondJsonOk(json)
        }

        // get vortragsart options
        get("/presentationtypes") {
            call.logRequest()
            val json = Serializer.stable.toJson(VortragsartDto.serializer().list, VortragsartDto.generate())
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