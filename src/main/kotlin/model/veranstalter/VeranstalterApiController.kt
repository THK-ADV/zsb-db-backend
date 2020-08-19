package model.veranstalter

import error_handling.HttpServerResponse
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.*
import utilty.*

fun Route.veranstalterApi() {
    route("veranstalter") {
        // get all
        get {
            call.logRequest()
            val result = VeranstalterDao.getAll(call.parameters["resolve_ids"] == "true")
            call.respond(HttpServerResponse.map(result))
        }

        // get one by id
        get("/{uuid}") {
            call.logRequest()
            val uuid = call.getParameterAsUuidOrNullAndRespondError("uuid") ?: return@get
            val result = VeranstalterDao.getById(uuid, call.parameters["resolve_ids"] == "true")
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
            val isDeleted = VeranstalterDao.delete(uuid)
            if (isDeleted)
                call.respondTextAsJson("Successfully deleted Veranstalter: $uuid")
            else
                call.respondTextAsJson("Couldn't find Veranstalter with id: $uuid", status = HttpStatusCode.NotFound)
        }
    }
}

private suspend fun postOrPut(call: ApplicationCall, isPost: Boolean = false) {
    call.logRequest()
    val veranstalterDto = call.receive<VeranstalterDto>()

    if (isPost) {
        if (call.checkIdAndRespondUsePutIfNotNull(veranstalterDto.uuid)) return
    } else
        if (call.checkIdAndRespondUsePostIfNull(veranstalterDto.uuid)) return

    val result = VeranstalterDao.createOrUpdate(veranstalterDto)
    call.respond(HttpServerResponse.map(result))
}