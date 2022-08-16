package model.bericht

import error_handling.CouldNotParseUuidException
import error_handling.UuidNotFound
import kotlinx.serialization.Serializable
import model.termin.Termin
import model.termin.TerminDto
import model.termin.Termine
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
    val event_id = reference("termin_id", Termine)
}

class Bericht(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var title by Berichte.title
    private var text by Berichte.text
    private var termin by Termin referencedOn Berichte.event_id

    companion object : UUIDEntityClass<Bericht>(Berichte) {

        fun save(dto: BerichtDto): Result<Bericht> = transaction {
            // validate ids
            val terminId = anyOrNull { UUID.fromString(dto.event_id) }
                ?: return@transaction Result.failure(
                    CouldNotParseUuidException("terminId for Bericht isn't valid.")
                )

            // get termin by id
            val termin = anyOrNull { Termin[terminId] }
                ?: return@transaction Result.failure(UuidNotFound("Could not find Termin with ID: $terminId"))

            // update/create
            val bericht = if (dto.uuid != null) {
                val uuid = anyOrNull { UUID.fromString(dto.uuid) }
                    ?: return@transaction Result.failure(CouldNotParseUuidException("UUID for Bericht not valid."))
                val old = anyOrNull { Bericht[uuid] }
                    ?: return@transaction Result.failure(UuidNotFound("Couldn't find Bericht with ID: $uuid"))

                old.update(dto, termin)
                Bericht[uuid]
            } else {
                new { update(dto, termin) }
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

    private fun update(dto: BerichtDto, termin: Termin) {
        this.text = dto.text
        this.title = dto.title
        this.termin = termin
    }

    fun toDto() = BerichtDto(id.value.toString(), title, text, termin.id.toString())

    fun toAtomicDto() = BerichtDto(id.value.toString(), title, text, termin.id.toString(), termin.toDto())
}

@Serializable
data class BerichtDto(
    val uuid: String?,
    val title: String,
    val text: String,
    val event_id: String,
    val event: TerminDto? = null
)