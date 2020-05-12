package utilty

import error_handling.HttpServerResponse
import io.ktor.application.ApplicationCall
import io.ktor.features.origin
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.httpMethod
import io.ktor.request.path
import io.ktor.response.respondText
import kotlinx.serialization.json.JsonElement
import mu.KotlinLogging

fun ApplicationCall.logRequest() {
    val log = ColoredLogging(KotlinLogging.logger {})
    val params = if (parameters.isEmpty()) "" else parameters.toString()
    log.info("${request.httpMethod.value} ${request.path()} $params from ${request.origin.remoteHost}")
}

suspend fun ApplicationCall.getParameterAsIntOrNullAndRespondError(param: String): Int? {
    val id = parameters[param]?.toIntOrNull()
    if (id == null) respondText("given $param must be an integer", ContentType.Text.Plain, HttpStatusCode.BadRequest)
    return id
}

suspend fun ApplicationCall.respondJsonOk(json: JsonElement) =
    this.respondText(json.toString(), ContentType.Application.Json, HttpStatusCode.OK)

suspend fun ApplicationCall.respond(response: HttpServerResponse) {
    this.respondText(response.text, response.type, response.status)
}

/**
 * @return true if the id is not given and a respond was send
 */
suspend fun ApplicationCall.checkIdAndRespondUsePostIfNull(id: Int?): Boolean {
    if (id == null) {
        this.respondText("ID not given. Use post to create.", ContentType.Text.Plain, HttpStatusCode.BadRequest)
        return true
    }
    return false
}

/**
 * @return true if the id is given and a respond was send
 */
suspend fun ApplicationCall.checkIdAndRespondUsePutIfNotNull(id: Int?): Boolean {
    if (id != null) {
        this.respondText("ID given. Use put to update existing data.", ContentType.Text.Plain, HttpStatusCode.BadRequest)
        return true
    }
    return false
}