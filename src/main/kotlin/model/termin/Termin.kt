package model.termin

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

object Termine : UUIDTable() {
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

class Termin(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var designation by Termine.designation
    private var host by Veranstalter referencedOn Termine.host_id
    private var category by Termine.category
    private var topic by Termine.topic
    private var date by Termine.date
    private var amountStudents by Termine.amountStudents
    private var level by Termine.level
    private var sequence by Termine.sequence
    private var runs by Termine.runs
    private var contactPerson by Termine.contactPerson

    companion object : UUIDEntityClass<Termin>(Termine) {
        fun save(dto: TerminDto): Result<Termin> = transaction {
            // validate ids
            val veranstalterId = anyOrNull { UUID.fromString(dto.host_id) }
                ?: return@transaction Result.failure(
                    CouldNotParseUuidException("veranstalter_id for Termin is not valid.")
                )

            // get related objects from db
            val veranstalter = anyOrNull { Veranstalter[veranstalterId] }
                ?: return@transaction Result.failure(UuidNotFound("Could not find Veranstalter with ID: $veranstalterId"))

            // finding a matched Termin is skipped here due to the unique

            // update/create Termin
            val termin = if (dto.uuid != null) {
                val uuid = anyOrNull { UUID.fromString(dto.uuid) }
                    ?: return@transaction Result.failure(CouldNotParseUuidException("UUID for Termin is not valid."))
                val old = anyOrNull { Termin[uuid] }
                    ?: return@transaction Result.failure(UuidNotFound("Couldn't find Termin with UUID: $uuid"))

                // update and safe
                old.update(dto, veranstalter)
                Termin[uuid]
            } else {
                new { update(dto, veranstalter) }
            }

            // return result
            Result.success(termin)
        }

        /**
         * Delete termin with [id] and all attached [Berichte]
         */
        fun delete(id: UUID): Boolean {
            val result = anyOrNull {
                transaction {
                    Berichte.deleteWhere { Berichte.event_id eq id }
                    Termine.deleteWhere { Termine.id eq id }
                }
            }

            return result != null
        }
    }

    private fun update(dto: TerminDto, veranstalter: Veranstalter) {
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

    fun toDto() = TerminDto(
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

    fun toAtomicDto() = TerminDto(
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
data class TerminDto(
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