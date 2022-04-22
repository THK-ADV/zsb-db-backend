package model.bericht

import error_handling.InternalDbException
import error_handling.UuidNotFound
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object BerichtDao {
    fun getAll(atomic: Boolean = false): Result<String> = transaction {
        val result = anyOrNull {
            Bericht.all().map {
                if (atomic) it.toAtomicDto() else it.toDto()
            }
        }

        if (result == null)
            Result.failure(InternalDbException("Error while trying to get all Berichte from db."))
        else
            Result.success(mapJsonResultList(result))
    }

    fun getById(uuid: UUID, atomic: Boolean = false): Result<String> = transaction {
        val result = anyOrNull {
            if (atomic) Bericht[uuid].toAtomicDto() else Bericht[uuid].toDto()
        }

        if (result == null)
            Result.failure(UuidNotFound("Couldn't find Bericht with ID: $uuid"))
        else
            Result.success(mapJsonResult(result))
    }

    fun createOrUpdate(berichtDto: BerichtDto): Result<String> = transaction {
        Bericht.save(berichtDto).map { mapJsonResult(it.toDto()) }
    }

    fun delete(berichtId: UUID): Boolean = Bericht.delete(berichtId)

    private fun mapJsonResult(result: BerichtDto) =
        Json.encodeToString(result)

    private fun mapJsonResultList(result: List<BerichtDto>) =
        Json.encodeToString(result)
}