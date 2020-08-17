package model.veranstalter

import error_handling.InternalDbException
import error_handling.VeranstalterIdNotValidException
import kotlinx.serialization.list
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.Serializer
import utilty.fromTry
import java.util.*

object VeranstalterDao {
    fun getAll(atomic: Boolean = false): Result<String> = transaction {
        val result = fromTry {
            Veranstalter.all().map {
                if (atomic) it.toAtomicDto() else it.toDto()
            }
        }

        if (result == null)
            Result.failure(InternalDbException("Error while trying to get all Veranstalter from db."))
        else
            Result.success(Serializer.stable.toJson(VeranstalterDto.serializer().list, result).toString())
    }

    fun getById(id: UUID, atomic: Boolean = false) = transaction {
        val result = fromTry {
            if (atomic) Veranstalter[id].toAtomicDto() else Veranstalter[id].toDto()
        }

        if (result == null)
            Result.failure(VeranstalterIdNotValidException("ID ($id) is not a valid uuid for Veranstalter."))
        else
            Result.success(mapJsonResult(result))
    }

    fun createOrUpdate(veranstalterDto: VeranstalterDto) = transaction {
        Veranstalter.save(veranstalterDto).map { mapJsonResult(it.toDto()) }
    }

    fun delete(veranstalterId: UUID): Boolean = Veranstalter.delete(veranstalterId)

    private fun mapJsonResult(result: VeranstalterDto) =
        Serializer.stable.toJson(VeranstalterDto.serializer(), result).toString()
}