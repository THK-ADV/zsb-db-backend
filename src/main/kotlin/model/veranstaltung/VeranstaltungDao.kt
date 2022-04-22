package model.veranstaltung

import error_handling.InternalDbException
import error_handling.UuidNotFound
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object VeranstaltungDao {
    fun getAll(atomic: Boolean = false): Result<String> = transaction {
        val result = anyOrNull {
            Veranstaltung.all().map {
                if (atomic) it.toAtomicDto() else it.toDto()
            }
        }

        if (result == null)
            Result.failure(InternalDbException("Error while trying to get all Veranstaltungen from db."))
        else
            Result.success(mapJsonResultList(result))
    }

    fun getById(id: UUID, atomic: Boolean = false): Result<String> = transaction {
        val result = anyOrNull {
            if (atomic) Veranstaltung[id].toAtomicDto() else Veranstaltung[id].toDto()
        }

        if (result == null)
            Result.failure(UuidNotFound("Couldn't find Veranstaltung with UUID: $id"))
        else
            Result.success(mapJsonResult(result))

    }

    fun createOrUpdate(veranstaltungDto: VeranstaltungDto): Result<String> = transaction {
        Veranstaltung.save(veranstaltungDto).map { mapJsonResult(it.toDto()) }
    }


    fun delete(uuid: UUID): Boolean = Veranstaltung.delete(uuid)

    private fun mapJsonResult(result: VeranstaltungDto) =
        Json.encodeToString(result)

    private fun mapJsonResultList(result: List<VeranstaltungDto>) =
        Json.encodeToString(result)
}
