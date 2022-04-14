package model.institution

import error_handling.InternalDbException
import error_handling.UuidNotFound
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object InstitutionDao {
    fun getAll(atomic: Boolean = false): Result<String> = transaction {
        val result = anyOrNull {
            Institution.all().map {
                if (atomic) it.toAtomicDto() else it.toDto()
            }
        }

        if (result == null)
            Result.failure(InternalDbException("Error while trying to get all Institutionen from db."))
        else
            Result.success(Json.encodeToString(result))
    }

    fun getById(id: UUID, atomic: Boolean = false): Result<String> = transaction {
        val result = anyOrNull {
            if (atomic) Institution[id].toAtomicDto() else Institution[id].toDto()
        }

        if (result == null)
            Result.failure(UuidNotFound("Couldn't fin Institution with ID: $id"))
        else
            Result.success(mapJsonResult(result))
    }

    fun createOrUpdate(institutionDto: InstitutionDto): Result<String> = transaction {
        Institution.save(institutionDto).map { mapJsonResult(it.toDto()) }
    }

    fun delete(institutionsId: UUID): Boolean = Institution.delete(institutionsId)

    private fun mapJsonResult(result: InstitutionDto) =
        Json.encodeToString(result)
}
