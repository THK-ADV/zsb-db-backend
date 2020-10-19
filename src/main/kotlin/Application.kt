import database.DbSettings
import database.generateDummyData
import database.recreateDatabase
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import legacy_import.CsvImport
import model.adresse.adressenApi
import model.bericht.berichteApi
import model.institution.institutionenApi
import model.kontakt.kontakteApi
import model.ort.orteApi
import model.schule.schulenApi
import model.veranstalter.veranstalterApi
import model.veranstaltung.veranstaltungenApi
import mu.KotlinLogging
import utilty.ColoredLogging
import utilty.anyOrNull
import word.wordApi
import java.io.File

val log = ColoredLogging(KotlinLogging.logger {})
const val RESOURCE_PATH = "src\\main\\resources\\signatures\\"

fun Application.main() {
    // connect to db
    DbSettings.connect(environment)

    // load csv file or dummy data
    anyOrNull {
        val file = "schule_demo_file.csv"
        CsvImport(File("src\\main\\resources\\legacy_import\\$file")).parseSchule()
        log.info("loaded data from '$file'")
    } ?: log.warn("Couldn't import CSV-File!")

    configureServer(this)
}

fun main() {
    // connect to db
    DbSettings.db

    recreateDatabase()

    // load csv file
    anyOrNull {
        val file = "schule_demo_file.csv"
        CsvImport(File("src\\main\\resources\\legacy_import\\$file")).parseSchule()
        log.info("loaded data from '$file'")
    } ?: log.warn("Couldn't import CSV-File!")

    // fill some dummy data for new tables
    generateDummyData()

    val server = embeddedServer(Netty, port = 9000) {
        configureServer(this)
    }

    log.info("## Start Server ##")
    server.start(wait = true)
}

fun configureServer(server: Application) {
    server.install(DefaultHeaders)
    server.install(Compression)
    server.install(CallLogging)
    server.install(CORS) {
        anyHost()
        method(HttpMethod.Put)
        method(HttpMethod.Post)
        method(HttpMethod.Get)
        method(HttpMethod.Delete)
        method(HttpMethod.Options)
    }
    server.install(Routing) {
        index()
        schulenApi()
        adressenApi()
        orteApi()
        kontakteApi()
        institutionenApi()
        veranstalterApi()
        veranstaltungenApi()
        berichteApi()
        wordApi()
    }
    server.install(ContentNegotiation) {
        serialization(
            contentType = ContentType.Application.Json,
            json = Json(
                DefaultJsonConfiguration.copy(
                    prettyPrint = true
                )
            )
        )
    }
}
