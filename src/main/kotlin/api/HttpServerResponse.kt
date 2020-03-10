package api

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode


data class HttpServerResponse(val text: String, val type: ContentType, val status: HttpStatusCode) {

    companion object {
        fun map(result: Result<String>, successStatus: HttpStatusCode = HttpStatusCode.OK): HttpServerResponse {
            result.onSuccess {
                return HttpServerResponse(it, ContentType.Application.Json, successStatus)
            }

            val exception = result.exceptionOrNull()
            if (exception == null || exception !is ZsbException)
                return HttpServerResponse("Unknown error.", ContentType.Text.Plain, HttpStatusCode.InternalServerError)

            val (failureMsg, statusCode) = when(exception) {
                is NotAuthorizedException -> (exception.message ?: "Not authorized.") to HttpStatusCode.Forbidden
                is MailNotValidException -> (exception.message ?: "Mail is not valid.") to HttpStatusCode.BadRequest
            }

            return HttpServerResponse(failureMsg, ContentType.Text.Plain, statusCode)
        }
    }
}