package model.institution

import error_handling.HttpServerResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import utilty.*

fun Route.institutionenApi() {
    route("institutions") {

        // get all
        get {
            call.logRequest()
            val result = InstitutionDao.getAll(call.parameters["resolve_ids"] == "true")
            call.respond(HttpServerResponse.map(result))
        }

        // get one by id
        get("/{uuid}") {
            call.logRequest()
            val uuid = call.getParameterAsUuidOrNullAndRespondError("uuid") ?: return@get
            val result = InstitutionDao.getById(uuid, call.parameters["resolve_ids"] == "true")
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
            val isDeleted = InstitutionDao.delete(uuid)
            if (isDeleted)
                call.respondTextAsJson("Successfully deleted $uuid")
            else
                call.respondTextAsJson("Couldn't find Institution with id: $uuid", status = HttpStatusCode.NotFound)
        }
    }
}

private suspend fun postOrPut(call: ApplicationCall, isPost: Boolean = false) {
    call.logRequest()
    val institutionDto = call.receive<InstitutionDto>()

    if (isPost) {
        if (call.checkIdAndRespondUsePutIfNotNull(institutionDto.uuid)) return
    } else
        if (call.checkIdAndRespondUsePostIfNull(institutionDto.uuid)) return

    val result = InstitutionDao.createOrUpdate(institutionDto)
    call.respond(HttpServerResponse.map(result))
}
