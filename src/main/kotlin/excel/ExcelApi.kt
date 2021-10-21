package excel

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
    route("downloadschools") {
        post {
            call.logRequest()
            val schoolDto = call.receive<SchuleDto>()
            val fileId = UUID.randomUUID()
            val file = File("$fileId.xls")
            val generator = ExcelGenerator(file)
            val result = generator.generateSheet(schoolDto)

            call.respondFile(file)

            file.delete()

        }
    }
}