package model.communication

import MailSettings
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.kontakt.KontaktDao
import model.kontakt.enum.KontaktFunktion
import model.schule.SchuleDao
import utilty.logRequest
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

fun Route.mailApi(mailSettings: MailSettings) = route("email") {
    val mail = MailerService(mailSettings)

    fun parseItems(part: PartData.FormItem): List<String> {
        return part.value.drop(1).dropLast(1).split(",").map { it.drop(1).dropLast(1) }
    }

    post {
        call.logRequest()
        val content = call.receiveMultipart()
        var msg = ""
        var subject = ""
        val addressees = mutableListOf<String>()
        val schoolIds = mutableListOf<String>()
        val attachments = mutableListOf<File>()
        content.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "msg" -> msg = part.value
                        "subject" -> subject = part.value
                        "addressees" -> addressees.addAll(parseItems(part))
                        "schoolIds" -> schoolIds.addAll(parseItems(part))
                    }
                }

                is PartData.FileItem -> {
                    val fileName = (part.originalFileName ?: "unknown").replace(Regex("[^a-zA-Z0-9._-]"), "_")
                    val uploadsDir = File("uploads")
                    if (!uploadsDir.exists()) {
                        uploadsDir.mkdirs()
                    }
                    val path = Paths.get("uploads", fileName)
                    try {
                        val fileContent = part.streamProvider().readAllBytes()
                        val newPath = Files.write(path, fileContent)
                        attachments.add(newPath.toFile())
                    } catch (e: IOException) {
                        println("Fehler beim Speichern der Datei: ${e.message}")
                    }
                }
                else -> {
                    println("Unknown Part: ${part.name}")
                }
            }
            part.dispose()
        }

        val resolvedAddressees = mutableListOf<String>()
        schoolIds.forEach { id ->
            val school = SchuleDao.getById(UUID.fromString(id))
            var sendable = false
            addressees.forEach { feature ->
                if (school.contacts_ids.isNotEmpty()) {
                    school.contacts_ids.forEach { id ->
                        val uuid = UUID.fromString(id)
                        val kontakt = KontaktDao.getById(uuid)
                        val function = KontaktFunktion.fromDesc(feature)
                        if (kontakt.feature == function.id) {
                            resolvedAddressees.add(kontakt.email)
                            sendable = true
                        }
                    }
                }
                if (feature == "Standard") {
                    resolvedAddressees.add(school.email)
                    sendable = true
                }
            }
            if (!sendable) {
                resolvedAddressees.add(school.email)
            }
        }
        val dto = MailDto(msg, resolvedAddressees, schoolIds, subject)
        val result = mail.sendMail(dto, attachments)
        println(result)
        attachments.forEach { file ->
            file.delete()
        }
        call.respond(HttpStatusCode.OK)
    }

    post("addressees") {
        val ids = call.receive<List<String>>()
        val allContactFunctions = mutableSetOf<String>()
        val uuids = ids.map { UUID.fromString(it) }
        allContactFunctions.add("Standard")
        val contactFeatures = SchuleDao.getContactsByIds(uuids)
        contactFeatures.forEach { feature ->
            val function = KontaktFunktion.getDescById(feature)
            allContactFunctions.add(function)
        }
        call.respond(HttpStatusCode.OK, allContactFunctions)
    }
}
