import api.adresseApi
import api.schuleApi
import database.DbSettings
import database.recreateTablesAndFillWithDummyData
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.routing.Routing
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.serialization.serialization
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.json.Json


fun Application.main() {
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
    server.install(Routing) {
        schuleApi()
        adresseApi()
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