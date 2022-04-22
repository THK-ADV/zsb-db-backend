package model.bericht

import error_handling.HttpServerResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import utilty.*

fun Route.berichteApi() {
    route("reports") {

        // get all
        get {
            call.logRequest()
            val result = BerichtDao.getAll(call.parameters["resolve_ids"] == "true")
            call.respond(HttpServerResponse.map(result))
        }

        // get on by id
        get("/{uuid}") {
            call.logRequest()
            val uuid = call.getParameterAsUuidOrNullAndRespondError() ?: return@get
            val result = BerichtDao.getById(uuid, call.parameters["resolve_ids"] == "true")
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
            val isDeleted = BerichtDao.delete(uuid)
            if (isDeleted)
                call.respondTextAsJson("Successfully deleted $uuid")
            else
                call.respondTextAsJson("Couldn't find Bericht with id: $uuid", status = HttpStatusCode.NotFound)
        }
    }
}

private suspend fun postOrPut(call: ApplicationCall, isPost: Boolean = false) {
    call.logRequest()
    val berichtDto = call.receive<BerichtDto>()

    if (isPost) {
        if (call.checkIdAndRespondUsePutIfNotNull(berichtDto.uuid)) return
    } else
        if (call.checkIdAndRespondUsePostIfNull(berichtDto.uuid)) return

    val result = BerichtDao.createOrUpdate(berichtDto)
    call.respond(HttpServerResponse.map(result))
}
