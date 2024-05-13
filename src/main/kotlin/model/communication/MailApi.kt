package model.communication

import MailSettings
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.kontakt.KontaktDao
import model.kontakt.enum.KontaktFunktion
import model.schule.Schule
import model.schule.SchuleDto
import utilty.logRequest
import java.util.*

fun Route.mailApi(mailSettings: MailSettings) = route("email") {
    val mail = MailerService(mailSettings)

    post {
        call.logRequest()
        val content = call.receive<MailDto>()
        val addressees = mutableListOf<String>()
        val unsendables = mutableListOf<SchuleDto>()
        content.schools.forEach { school ->
            var sendable = false
            content.addressees.forEach { feature ->
                if (feature == "Standard") {
                    addressees.add(school.email)
                    sendable = true
                }
                if (school.contacts_ids.isNotEmpty()) {
                    school.contacts_ids.forEach { id ->
                        val uuid = UUID.fromString(id)
                        val kontakt = KontaktDao.getById(uuid)
                        val function = KontaktFunktion.fromDesc(feature)
                        if (kontakt.feature == function.id) {
                            addressees.add(kontakt.email)
                            sendable = true
                        }
                    }
                }
            }
            if (!sendable) {
                unsendables.add(school)
            }
        }
        // TODO: Wie sollen wir mit Schulen umgehen, die nur eine Mailadresse haben? Aktuell hat trotzdem die Adressatenauswahl Priorität. Man könnte es auch so machen, dass es bei diesen Schulen immer an diese Mail Adresse geht, egal, ob sie der Adressatenauswahl entspricht oder nicht.
        // mail.sendMail(content, adressees)
        call.respond(HttpStatusCode.OK, unsendables)
    }

    post("addressees") {
        val schools = call.receive<List<SchuleDto>>()
        val allContactFunctions = mutableSetOf<String>()
        schools.forEach { school ->
            if (school.email.isNotEmpty()) {
                allContactFunctions.add("Standard")
            }
            school.contacts_ids.forEach { id ->
                val uuid = UUID.fromString(id)
                val kontakt = KontaktDao.getById(uuid)
                val function = KontaktFunktion.getDescById(kontakt.feature ?: 0)
                allContactFunctions.add(function)
            }
        }
        call.respond(HttpStatusCode.OK, allContactFunctions)
    }
}
