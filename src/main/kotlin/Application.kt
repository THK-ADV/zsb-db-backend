import database.DbSettings
import excel.excelApi
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import model.address.adressenApi
import model.bericht.berichteApi
import model.communication.mailApi
import model.institution.institutionenApi
import model.kontakt.kontakteApi
import model.ort.orteApi
import model.schule.schoolsApi
import model.veranstalter.veranstalterApi
import model.veranstaltung.veranstaltungenApi
import mu.KotlinLogging
import utilty.ColoredLogging
import word.wordApi

val log = ColoredLogging(KotlinLogging.logger {})

fun Application.main() {
    DbSettings.connect(environment)
    configureServer(this, environment)
}

fun main() {
    // connect to db
    DbSettings.db

    /*recreateDatabase()

    anyOrNull {
        val fileName = "data-import.csv"
        val file = File("src/main/resources/legacy_import/$fileName")
        CsvImport.parseSchule(file)
        log.info("loaded data from '$fileName'")
    } ?: log.warn("Couldn't import CSV-File!")

    generateDummyData()*/

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
        excelApi()
        env?.let { mailApi(loadMailSettings(it)) }
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

data class MailSettings(val sender: String, val host: String, val timeout: Int, val chunkSize: Int)

private fun loadMailSettings(env: ApplicationEnvironment): MailSettings {
    val sender = env.config.propertyOrNull("mail.sender")
        ?.getString()
        ?.takeIf { it.isNotEmpty() }
        ?: throw Throwable("missing mail setting")
    val host = env.config.propertyOrNull("mail.host")
        ?.getString()
        ?.takeIf { it.isNotEmpty() }
        ?: throw Throwable("missing mail setting")
    val timeout = env.config.propertyOrNull("mail.timeout")
        ?.getString()
        ?.toIntOrNull()
        ?.takeIf { it > 0 }
        ?: throw Throwable("missing mail setting")
    val chunkSize = env.config.propertyOrNull("mail.chunkSize")
        ?.getString()
        ?.toIntOrNull()
        ?.takeIf { it > 0 }
        ?: throw Throwable("missing mail setting")

    return MailSettings(sender, host, timeout, chunkSize)
}