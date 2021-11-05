package excel

import error_handling.CouldNotGenerateExcelFileException
import error_handling.CouldNotGenerateSerialLetterException
import error_handling.HttpServerResponse
import io.ktor.application.*
import io.ktor.http.*
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
            val schools = call.receive<List<SchuleDto>>()
            val generator = ExcelGenerator()
            val result = generator.generateSheet(schools)

            call.response.headers.append("Content-Disposition", "attachment")
            call.respondBytes(result, ContentType("application","vnd.ms-excel"), HttpStatusCode.OK)

        }
    }
}