import database.DbSettings
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.routing.Routing
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.serialization.serialization
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.json.Json
import legacy_import.CsvImport
import model.address.adressenApi
import model.bericht.berichteApi
import model.institution.institutionenApi
import model.kontakt.kontakteApi
import model.ort.orteApi
import model.schule.schoolsApi
import model.veranstalter.veranstalterApi
import model.veranstaltung.veranstaltungenApi
import mu.KotlinLogging
import utilty.ColoredLogging
import utilty.anyOrNull
import word.wordApi
import java.io.File

val log = ColoredLogging(KotlinLogging.logger {})
const val RESOURCE_PATH = "signatures/"

fun Application.main() {
    DbSettings.connect(environment)
    configureServer(this)
}

fun main() {
    // connect to db
    DbSettings.db

    //recreateDatabase()

    {
        anyOrNull {
            val fileName = "data-import.csv"
            val file = File("src/main/resources/legacy_import/$fileName")
            CsvImport.parseSchule(file)
            log.info("loaded data from '$fileName'")
        } ?: log.warn("Couldn't import CSV-File!")
    }
    //generateDummyData()

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
        schoolsApi()
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
