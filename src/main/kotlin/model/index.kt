import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.index() = route("/") {
    get {
        call.respondText("It works!")
    }
}