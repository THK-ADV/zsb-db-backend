package kontakt

import error_handling.HttpServerResponse
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.put
import io.ktor.routing.route
import kotlinx.serialization.list
import utilty.*

fun Route.kontaktApi() {
    route("kontakte") {
        get {
            call.logRequest()
            val result = KontaktDao.getAll()
            val json = Serializer.stable.toJson(KontaktDto.serializer().list, result)
            call.respondJsonOk(json)
        }

        get("/funktion") {
            call.logRequest()
            val json = Serializer.stable.toJson(KontaktFunktionDto.serializer().list, KontaktFunktionDto.generate())
            call.respondJsonOk(json)
        }

        get("/anrede") {
            call.logRequest()
            val json = Serializer.stable.toJson(AnredeDto.serializer().list, AnredeDto.generate())
            call.respondJsonOk(json)
        }

        get("/{uuid}") {
            call.logRequest()
            val uuid = call.getParameterAsUuidOrNullAndRespondError("uuid") ?: return@get

            val kontakt = KontaktDao.getById(uuid)
            val json = Serializer.stable.toJson(KontaktDto.serializer(), kontakt)
            call.respondJsonOk(json)
        }

        put {
            call.logRequest()

//            val test = call.receive<String>()
//            ColoredLogging.LOG.debug(test)
//            ColoredLogging.LOG.debug("THE END")
//            call.respondText { "respond" }
            val kontaktDto = call.receive<KontaktDto>()
            val result = KontaktDao.createOrUpdate(kontaktDto)
            call.respond(HttpServerResponse.map(result))
        }
    }
}