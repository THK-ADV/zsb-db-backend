package excel

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import model.schule.SchuleDto
import utilty.logRequest

fun Route.excelApi() {
    route("downloadsheet") {
        post {
            call.logRequest()
            val schools = call.receive<List<SchuleDto>>()
            val generator = ExcelGenerator()
            val result = generator.generateSheet(schools)

            call.response.headers.append("Content-Disposition", "attachment")
            call.respondBytes(result, ContentType("application", "vnd.ms-excel"), HttpStatusCode.OK)
        }
    }
}