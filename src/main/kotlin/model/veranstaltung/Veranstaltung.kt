package model.veranstaltung

import error_handling.CouldNotParseUuidException
import error_handling.UuidNotFound
import kotlinx.serialization.Serializable
import model.bericht.Berichte
import model.veranstalter.Veranstalter
import model.veranstalter.VeranstalterDto
import model.veranstalter.VeranstalterTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object Veranstaltungen : UUIDTable() {
    val designation = text("bezeichnung")
    val host_id = reference("veranstalter_id", VeranstalterTable)
    val category = text("kategorie")
    val topic = text("thema")
    val date = text("datum")
    val amountStudents = text("anzahl_sus")
    val level = text("stufe")
    val sequence = text("ablauf_und_bewertung")
    val runs = text("anzahl_der_durchlaeufe")
    val contactPerson = text("ansprechpartner")
}

class Veranstaltung(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var designation by Veranstaltungen.designation
    private var host by Veranstalter referencedOn Veranstaltungen.host_id
    private var category by Veranstaltungen.category
    private var topic by Veranstaltungen.topic
    private var date by Veranstaltungen.date
    private var amountStudents by Veranstaltungen.amountStudents
    private var level by Veranstaltungen.level
    private var sequence by Veranstaltungen.sequence
    private var runs by Veranstaltungen.runs
    private var contactPerson by Veranstaltungen.contactPerson

    companion object : UUIDEntityClass<Veranstaltung>(Veranstaltungen) {
        fun save(dto: VeranstaltungDto): Result<Veranstaltung> = transaction {
            // validate ids
            val veranstalterId = anyOrNull { UUID.fromString(dto.host_id) }
                ?: return@transaction Result.failure(
                    CouldNotParseUuidException("veranstalter_id for Veranstaltung is not valid.")
                )

            // get related objects from db
            val veranstalter = anyOrNull { Veranstalter[veranstalterId] }
                ?: return@transaction Result.failure(UuidNotFound("Could not find Veranstalter with ID: $veranstalterId"))

            // finding a matched Veranstaltung is skipped here due to the unique

            // update/create Veranstaltung
            val veranstaltung = if (dto.uuid != null) {
                val uuid = anyOrNull { UUID.fromString(dto.uuid) }
                    ?: return@transaction Result.failure(CouldNotParseUuidException("UUID for Veranstaltung is not valid."))
                val old = anyOrNull { Veranstaltung[uuid] }
                    ?: return@transaction Result.failure(UuidNotFound("Couldn't find Veranstaltung with UUID: $uuid"))

                // update and safe
                old.update(dto, veranstalter)
                Veranstaltung[uuid]
            } else {
                new { update(dto, veranstalter) }
            }

            // return result
            Result.success(veranstaltung)
        }

        /**
         * Delete veranstaltung with [veranstaltungId] and all attached [Berichte]
         */
        fun delete(veranstaltungId: UUID): Boolean {
            val result = anyOrNull {
                transaction {
                    Berichte.deleteWhere { Berichte.event_id eq veranstaltungId }
                    Veranstaltungen.deleteWhere { Veranstaltungen.id eq veranstaltungId }
                }
            }

            return result != null
        }
    }

    private fun update(dto: VeranstaltungDto, veranstalter: Veranstalter) {
        this.designation = dto.designation
        this.host = veranstalter
        this.category = transformMultiSelect(dto.category)
        this.topic = dto.topic
        this.date = dto.date
        this.amountStudents = dto.amountStudents
        this.level = transformMultiSelect(dto.level)
        this.sequence = dto.sequence
        this.runs = dto.runs
        this.contactPerson = dto.contactPerson
    }

    fun toDto() = VeranstaltungDto(
        id.value.toString(),
        designation,
        host.id.value.toString(),
        transformMultiSelect(category),
        topic,
        date,
        amountStudents,
        transformMultiSelect(level),
        sequence,
        runs,
        contactPerson
    )

    fun toAtomicDto() = VeranstaltungDto(
        id.value.toString(),
        designation,
        host.id.value.toString(),
        transformMultiSelect(category),
        topic,
        date,
        amountStudents,
        transformMultiSelect(level),
        sequence,
        runs,
        contactPerson,
        host.toDto()
    )

    private val separator = ":"

    private fun transformMultiSelect(ids: List<Int>) = ids.fold("") {acc, it -> acc + separator + it}
    private fun transformMultiSelect(ids: String): List<Int> {
        val list = mutableListOf<Int?>()
        ids.split(separator).forEach { list.add(it.toIntOrNull()) }
        return list.filterNotNull()
    }
}

@Serializable
data class VeranstaltungDto(
    val uuid: String?,
    val designation: String,
    val host_id: String,
    val category: List<Int>,
    val topic: String,
    val date: String,
    val amountStudents: String,
    val level: List<Int>,
    val sequence: String,
    val runs: String,
    val contactPerson: String,
    val host: VeranstalterDto? = null
)