package model.bericht

import error_handling.CouldNotParseUuidException
import error_handling.UuidNotFound
import kotlinx.serialization.Serializable
import model.veranstaltung.Veranstaltung
import model.veranstaltung.VeranstaltungDto
import model.veranstaltung.Veranstaltungen
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object Berichte : UUIDTable() {
    val title = text("titel")
    val text = text("text")
    val veranstaltung_id = reference("veranstaltung_id", Veranstaltungen)
}

class Bericht(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var title by Berichte.title
    private var text by Berichte.text
    private var veranstaltung by Veranstaltung referencedOn Berichte.veranstaltung_id

    companion object : UUIDEntityClass<Bericht>(Berichte) {

        fun save(dto: BerichtDto): Result<Bericht> = transaction {
            // validate ids
            val veranstaltungId = anyOrNull { UUID.fromString(dto.veranstaltung_id) }
                ?: return@transaction Result.failure(
                    CouldNotParseUuidException("veranstaltungId for Bericht isn't valid.")
                )

            // get veranstaltung by id
            val veranstaltung = anyOrNull { Veranstaltung[veranstaltungId] }
                ?: return@transaction Result.failure(UuidNotFound("Could not find Veranstaltung with ID: $veranstaltungId"))

            // update/create
            val bericht = if (dto.uuid != null) {
                val uuid = anyOrNull { UUID.fromString(dto.uuid) }
                    ?: return@transaction Result.failure(CouldNotParseUuidException("UUID for Bericht not valid."))
                val old = anyOrNull { Bericht[uuid] }
                    ?: return@transaction Result.failure(UuidNotFound("Couldn't find Bericht with ID: $uuid"))

                old.update(dto, veranstaltung)
                Bericht[uuid]
            } else {
                new { update(dto, veranstaltung) }
            }

            Result.success(bericht)
        }

        fun delete(berichtId: UUID): Boolean {
            val result = anyOrNull { transaction {
                Berichte.deleteWhere { Berichte.id eq berichtId }
            } }

            return result != null
        }
    }

    private fun update(dto: BerichtDto, veranstaltung: Veranstaltung) {
        this.text = dto.text
        this.title = dto.titel
        this.veranstaltung = veranstaltung
    }

    fun toDto() = BerichtDto(id.value.toString(), title, text, veranstaltung.id.toString())

    fun toAtomicDto() = BerichtDto(id.value.toString(), title, text, veranstaltung.id.toString(), veranstaltung.toDto())
}

@Serializable
data class BerichtDto(
    val uuid: String?,
    val titel: String,
    val text: String,
    val veranstaltung_id: String,
    val veranstaltung: VeranstaltungDto? = null
)