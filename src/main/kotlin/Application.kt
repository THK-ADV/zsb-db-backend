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
import io.ktor.server.plugins.cors.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import legacy_import.CsvImport
import model.address.adressenApi
import model.communication.mailApi
import model.kontakt.kontakteApi
import model.ort.orteApi
import model.schule.schoolsApi
import model.termin.termineApi
import mu.KotlinLogging
import utilty.ColoredLogging
import word.wordApi
import java.io.File

val log = ColoredLogging(KotlinLogging.logger {})

fun Application.main() {
    DbSettings.connect(environment)
    configureServer(this, environment)
}

fun main() {
    // connect to db
    DbSettings.db

    recreateDatabase()

    val fileName = "data-import.csv"
    val file = File("src/main/resources/legacy_import/$fileName")
    CsvImport.parseSchool(file)
    log.info("loaded data from '$fileName'")

    val server = embeddedServer(Netty, port = 9000) {
        configureServer(this, null)
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
    }
    server.install(Routing) {
        index()
        schoolsApi()
        adressenApi()
        orteApi()
        kontakteApi()
        termineApi()
        wordApi()
        excelApi()
        env?.let { mailApi(MailSettings.fromEnvironment(it)) }
    }
    server.install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}
