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
import model.schule.SchuleDao
import model.schule.SchuleDto
import utilty.logRequest
import java.util.*

fun Route.mailApi(mailSettings: MailSettings) = route("email") {
    val mail = MailerService(mailSettings)

    post {
        call.logRequest()
        val content = call.receive<MailDto>()
        val addressees = mutableListOf<String>()
        content.schoolIds.forEach { id ->
            val school = SchuleDao.getById(UUID.fromString(id))
            var sendable = false
            content.addressees.forEach { feature ->
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
                if (feature == "Standard") {
                    addressees.add(school.email)
                    sendable = true
                }
            }
            if (!sendable) {
                addressees.add(school.email)
            }
        }
        mail.sendMail(content, addressees)
        call.respond(HttpStatusCode.OK)
    }

    post("addressees") {
        val start = System.currentTimeMillis()
        val ids = call.receive<List<String>>()
        val allContactFunctions = mutableSetOf<String>()
        val uuids = ids.map { UUID.fromString(it) }
        allContactFunctions.add("Standard")
        val contactFeatures = SchuleDao.getContactsByIds(uuids)
        contactFeatures.forEach { feature ->
            val function = KontaktFunktion.getDescById(feature)
            allContactFunctions.add(function)
        }
        val end = System.currentTimeMillis()
        println(end - start)
        call.respond(HttpStatusCode.OK, allContactFunctions)
    }
}
