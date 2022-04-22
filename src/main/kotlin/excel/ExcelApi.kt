package excel

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
            call.respondBytes(result, ContentType("application","vnd.ms-excel"), HttpStatusCode.OK)
        }
    }
}