package word

import error_handling.CouldNotGenerateSerialLetterException
import error_handling.HttpServerResponse
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respondFile
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.serialization.list
import utilty.*
import word.enum.ZsbSignaturDto
import java.io.File
import java.util.*

fun Route.wordApi() {
    route("serialletter") {
        post {
            call.logRequest()
            val serialLetterDto = call.receive<SerialLetterDto>()
            val fileId = UUID.randomUUID()
            val file = File("$fileId.doc")
            val generator = WordGenerator(file)
            val result = generator.generateLetter(serialLetterDto)

            if (!result) {
                val error = HttpServerResponse.map(
                    Result.failure(CouldNotGenerateSerialLetterException("Word generation currently not working."))
                )
                ColoredLogging.LOG.error("Could not generate letter.")
                call.respond(error)
                return@post
            }

            call.respondFile(file)

            file.delete()
        }
    }

    get("assets") {
        call.logRequest()
        val json = Serializer.stable.toJson(ZsbSignaturDto.serializer().list, ZsbSignaturDto.generate())
        call.respondJsonOk(json)
    }
}