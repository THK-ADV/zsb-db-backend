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
import java.util.*

fun Route.wordApi() {
    route("serialletter") {
        post {
            call.logRequest()
            val serialLetterDto = call.receive<SerialLetterDto>()
            //val file = File("$fileId.doc")
            //val path = "$fileId.doc"
            //val file = File(path)
            //TODO: Pfad anpassen
            val generator = WordGenerator(template)
            val result = generator.generateLetter(serialLetterDto)

            if (!result) {
                val error = HttpServerResponse.map(
                    Result.failure(CouldNotGenerateSerialLetterException("Word generation currently not working."))
                )
                ColoredLogging.LOG.error("Could not generate letter.")
                call.respond(error)
                return@post
            }

            call.respondText("Datei wurde erstellt.")
            //call.respondFile(file)

            //file.delete()
        }
    }

    get("assets") {
        call.logRequest()
        val json = Json.encodeToJsonElement(ZsbSignaturDto.generate())
        call.respondJsonOk(json)
    }
}
