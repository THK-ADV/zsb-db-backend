package word

import error_handling.CouldNotGenerateSerialLetterException
import error_handling.HttpServerResponse
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import utilty.ColoredLogging
import utilty.logRequest
import utilty.respond
import utilty.respondJsonOk
import word.enum.ZsbSignaturDto
import java.io.File

fun Route.wordApi(letterPath: String) {
    route("serialletter") {
        post {
            call.logRequest()
            val serialLetterDto = call.receive<SerialLetterDto>()
            val template = File(letterPath)
            println(template.path)
            val generator = WordGenerator(template)
            val file = generator.generateLetter(serialLetterDto)

            if (file == null) {
                val error = HttpServerResponse.map(
                    Result.failure(CouldNotGenerateSerialLetterException("Word generation currently not working."))
                )
                ColoredLogging.LOG.error("Could not generate letter.")
                call.respond(error)
                return@post
            }
            call.respondFile(file)
            //file.delete()
        }
    }

    get("assets") {
        call.logRequest()
        val json = Json.encodeToJsonElement(ZsbSignaturDto.generate())
        call.respondJsonOk(json)
    }
}
