package model.termin

import error_handling.InternalDbException
import error_handling.UuidNotFound
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object TerminDao {
    fun getAll(atomic: Boolean = false): Result<String> = transaction {
        val result = anyOrNull {
            Termin.all().map {
                if (atomic) it.toAtomicDto() else it.toDto()
            }
        }

        if (result == null)
            Result.failure(InternalDbException("Error while trying to get all Termine from db."))
        else
            Result.success(mapJsonResultList(result))
    }

    fun getById(id: UUID, atomic: Boolean = false): Result<String> = transaction {
        val result = anyOrNull {
            if (atomic) Termin[id].toAtomicDto() else Termin[id].toDto()
        }

        if (result == null)
            Result.failure(UuidNotFound("Couldn't find Termin with UUID: $id"))
        else
            Result.success(mapJsonResult(result))

    }

    fun createOrUpdate(terminDto: TerminDto): Result<String> = transaction {
        Termin.save(terminDto).map { mapJsonResult(it.toDto()) }
    }


    fun delete(uuid: UUID): Boolean = Termin.delete(uuid)

    private fun mapJsonResult(result: TerminDto) =
        Json.encodeToString(result)

    private fun mapJsonResultList(result: List<TerminDto>) =
        Json.encodeToString(result)
}
