import adresse.adresseApi
import database.DbSettings
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
import ort.ortApi
import schule.schuleApi


fun Application.main() {
    // connect to db
    DbSettings.db
    recreateTablesAndFillWithDummyData()

    configureServer(this)
}

fun main() {
    // connect to db
    DbSettings.db

    recreateTablesAndFillWithDummyData()

    embeddedServer(Netty, port = 8080) {
        configureServer(this)
    }.start(wait = true)
}

fun configureServer(server: Application) {
    server.install(DefaultHeaders)
    server.install(Compression)
    server.install(CallLogging)
    server.install(CORS) {
        method(HttpMethod.Options)
        header(HttpHeaders.XForwardedProto)
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }
    server.install(Routing) {
        schuleApi()
        adresseApi()
        ortApi()
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