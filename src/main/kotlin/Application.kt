import database.DbSettings
import database.clearDatabase
import database.recreateTablesAndFillWithDummyData
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.routing.Routing
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.serialization.serialization
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.json.Json
import legacy_import.CsvImport
import model.adresse.adressenApi
import model.institution.institutionenApi
import model.kontakt.kontakteApi
import model.ort.orteApi
import model.schule.schulenApi
import model.veranstalter.veranstalterApi
import model.veranstaltung.veranstaltungenApi
import mu.KotlinLogging
import utilty.ColoredLogging
import utilty.fromTry
import java.io.File

val log = ColoredLogging(KotlinLogging.logger {})

fun Application.main() {
    // connect to db
    DbSettings.connect(environment)

    // load csv file or dummy data
    fromTry {
        val file = "schule_demo_file.csv"
        CsvImport(File("src\\main\\resources\\legacy_import\\$file")).parseSchule()
        log.info("loaded data from '$file'")
    } ?: log.warn("Couldn't import CSV-File!")

    configureServer(this)
}

fun main() {
    // connect to db
    DbSettings.db

    // empty db
    clearDatabase()

    // load csv file or dummy data
    fromTry {
        val file = "schule_demo_file.csv"
        CsvImport(File("src\\main\\resources\\legacy_import\\$file")).parseSchule()
        log.info("loaded data from '$file'")
    } ?: recreateTablesAndFillWithDummyData()


    val server = embeddedServer(Netty, port = 8080) {
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
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        header(HttpHeaders.XForwardedProto)
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }
    server.install(Routing) {
        schulenApi()
        adressenApi()
        orteApi()
        kontakteApi()
        institutionenApi()
        veranstalterApi()
        veranstaltungenApi()
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