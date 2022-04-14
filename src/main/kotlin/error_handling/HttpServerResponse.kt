package error_handling

import io.ktor.http.*

data class HttpServerResponse(val text: String, val type: ContentType, val status: HttpStatusCode) {

    companion object {
        fun map(result: Result<String>, successStatus: HttpStatusCode = HttpStatusCode.OK): HttpServerResponse {
            result.onSuccess {
                return HttpServerResponse(it, ContentType.Application.Json, successStatus)
            }

            val exception = result.exceptionOrNull()
            if (exception == null || exception !is ZsbException)
                return HttpServerResponse("Unknown error.", ContentType.Text.Plain, HttpStatusCode.InternalServerError)

            val (failureMsg, statusCode) = when (exception) {
                is NotAuthorizedException -> exception.message to HttpStatusCode.Forbidden
                is MailNotValidException -> exception.message to HttpStatusCode.BadRequest
                is SchoolTypeNotValidException -> exception.message to HttpStatusCode.BadRequest
                is CityIdNotFoundException -> exception.message to HttpStatusCode.BadRequest
                is AddressIdNotFoundException -> exception.message to HttpStatusCode.NotFound
                is SchoolIdNotFoundException -> exception.message to HttpStatusCode.NotFound
                is CouldNotParseUuidException -> exception.message to HttpStatusCode.BadRequest
                is AmountStudentsNotValidException -> exception.message to HttpStatusCode.BadRequest
                is ContactIdNotValidException -> exception.message to HttpStatusCode.BadRequest
                is SalutationNotValidException -> exception.message to HttpStatusCode.BadRequest
                is InstitutionIdNotValidException -> exception.message to HttpStatusCode.BadRequest
                is InternalDbException -> exception.message to HttpStatusCode.InternalServerError
                is HostIdNotValidException -> exception.message to HttpStatusCode.BadRequest
                is UuidNotFound -> exception.message to HttpStatusCode.NotFound
                is TooManyHostsException -> exception.message to HttpStatusCode.BadRequest
                is CouldNotGenerateSerialLetterException -> exception.message to HttpStatusCode.InternalServerError
                is CooperationPartnerNotValidException -> exception.message to HttpStatusCode.BadRequest
                else -> return HttpServerResponse(
                    "Unknown error.",
                    ContentType.Text.Plain,
                    HttpStatusCode.InternalServerError
                )
            }

            return HttpServerResponse(failureMsg, ContentType.Text.Plain, statusCode)
        }
    }
}