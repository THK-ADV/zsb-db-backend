package model.veranstalter

import error_handling.HostIdNotValidException
import error_handling.InternalDbException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object VeranstalterDao {
    fun getAll(atomic: Boolean = false): Result<String> = transaction {
        val result = anyOrNull {
            Veranstalter.all().map {
                if (atomic) it.toAtomicDto() else it.toDto()
            }
        }

        if (result == null)
            Result.failure(InternalDbException("Error while trying to get all Veranstalter from db."))
        else
            Result.success(Json.encodeToString(result))
    }

    fun getById(id: UUID, atomic: Boolean = false) = transaction {
        val result = anyOrNull {
            if (atomic) Veranstalter[id].toAtomicDto() else Veranstalter[id].toDto()
        }

        if (result == null)
            Result.failure(HostIdNotValidException("ID ($id) is not a valid uuid for Veranstalter."))
        else
            Result.success(mapJsonResult(result))
    }

    fun createOrUpdate(veranstalterDto: VeranstalterDto) = transaction {
        Veranstalter.save(veranstalterDto).map { mapJsonResult(it.toDto()) }
    }

    fun delete(veranstalterId: UUID): Boolean = Veranstalter.delete(veranstalterId)

    private fun mapJsonResult(result: VeranstalterDto) =
        Json.encodeToString(result)
}
