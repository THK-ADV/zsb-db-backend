package model.veranstaltung

import error_handling.HttpServerResponse
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.*
import utilty.*

fun Route.veranstaltungenApi() {
    route("veranstaltungen") {

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
        post { postOrtPut(call, isPost = true) }

        // update
        put { postOrtPut(call) }

        // delete
        delete("/{uuid}") {
            call.logRequest()
            val uuid = call.getParameterAsUuidOrNullAndRespondError("uuid") ?: return@delete
            val result = VeranstaltungDao.delete(uuid)

            if (result)
                call.respondTextAsJson("Successfully deleted $uuid")
            else
                call.respondTextAsJson("Couldn't find Veranstaltung with id: $uuid", status = HttpStatusCode.NotFound)
        }
    }
}

private suspend fun postOrtPut(call: ApplicationCall, isPost: Boolean = false) {
    call.logRequest()
    val veranstaltungDto = call.receive<VeranstaltungDto>()

    if (isPost) {
        if (call.checkIdAndRespondUsePutIfNotNull(veranstaltungDto.uuid)) return
    } else
        if (call.checkIdAndRespondUsePostIfNull(veranstaltungDto.uuid)) return

    val result = VeranstaltungDao.createOrUpdate(veranstaltungDto)
    call.respond(HttpServerResponse.map(result))
}