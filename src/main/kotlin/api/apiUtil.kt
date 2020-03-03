package api

import io.ktor.application.ApplicationCall
import io.ktor.features.origin
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.httpMethod
import io.ktor.request.path
import io.ktor.response.respondText
import mu.KotlinLogging

fun ApplicationCall.logRequest() {
    val apiLogger = KotlinLogging.logger {}
    val params = if (parameters.isEmpty()) "" else parameters.toString()
    apiLogger.info { "${request.httpMethod.value} ${request.path()} $params from ${request.origin.remoteHost}" }
}

suspend fun ApplicationCall.getParameterAsIntOrNullAndRespondError(param: String): Int? {
    val id = parameters[param]?.toIntOrNull()
    if (id == null) respondText("given $param must be an integer", ContentType.Text.Plain, HttpStatusCode.BadRequest)
    return id
}