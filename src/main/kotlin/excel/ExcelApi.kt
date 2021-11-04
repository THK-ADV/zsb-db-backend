package excel

import error_handling.CouldNotGenerateExcelFileException
import error_handling.CouldNotGenerateSerialLetterException
import error_handling.HttpServerResponse
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import model.schule.SchuleDto
import utilty.*
import java.io.File
import java.util.*

fun Route.excelApi() {
    route("downloadsheet") {
        post {
            call.logRequest()
            val schoolDtos = call.receive<List<SchuleDto>>()
            val fileId = UUID.randomUUID()
            val file = File("$fileId.xls")
            val generator = ExcelGenerator(file)
            val result = generator.generateSheet(schoolDtos)

            if(!result) {
                val error = HttpServerResponse.map(
                    Result.failure(CouldNotGenerateExcelFileException("Excel generation currently not working."))
                )
                ColoredLogging.LOG.error("Could not generate file.")
                call.respond(error)
                return@post
            }

            call.respondFile(file)

            file.delete()

        }
    }
}