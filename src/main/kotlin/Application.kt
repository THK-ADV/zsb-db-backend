import database.DbSettings
import database.recreateDatabase
import excel.excelApi
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import legacy_import.CsvImport
import model.address.adressenApi
import model.communication.mailApi
import model.kontakt.kontakteApi
import model.ort.orteApi
import model.schule.schoolsApi
import model.termin.kontakte.kontakteHochschuleApi
import model.termin.kontakte.kontakteSchuleApi
import model.termin.termineApi
import mu.KotlinLogging
import utilty.ColoredLogging
import word.wordApi
import java.io.File

val log = ColoredLogging(KotlinLogging.logger {})
var importPath = "src/main/resources/legacy_import/data-import.csv"

fun Application.main() {
    DbSettings.connect(environment)
    configureServer(this, environment)
    importPath = environment.config.propertyOrNull("import.path")?.getString() ?: "src/main/resources/legacy_import/data-import.csv"
    //bootstrapDb(importPath)
}

fun bootstrapDb(csvPath: String) {
    recreateDatabase()
    val file = File(csvPath)
    CsvImport.parseSchool(file)
}

fun main() {
    // connect to db
    DbSettings.db

    bootstrapDb(importPath)
    val server = embeddedServer(Netty, port = 9000) {
        log.info(environment.config.propertyOrNull("ktor.deployment.port")?.getString())
        configureServer(this, environment)
    }

    log.info("## Start Server ##")
    server.start(wait = true)
}

fun configureServer(server: Application, env: ApplicationEnvironment?) {
    server.install(DefaultHeaders)
    server.install(Compression)
    server.install(CallLogging)
    server.install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowNonSimpleContentTypes = true
    }
    server.install(Routing) {
        index()
        schoolsApi()
        adressenApi()
        orteApi()
        kontakteApi()
        termineApi()
        kontakteSchuleApi()
        kontakteHochschuleApi()
        wordApi(env?.config?.propertyOrNull("letter.path")?.getString() ?: "src/main/resources/files/serialletter-template.docx")
        excelApi()
        env?.let { mailApi(MailSettings.fromEnvironment(it)) }
    }
    server.install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}
