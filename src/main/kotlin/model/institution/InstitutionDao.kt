package model.institution

import error_handling.InstitutionIdNotValidException
import error_handling.InternalDbException
import kotlinx.serialization.list
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.Serializer
import utilty.fromTry
import java.util.*

object InstitutionDao {
    fun getAll(atomic: Boolean = false): Result<String> = transaction {
        val result = fromTry { Institution.all().map {
            if (atomic) it.toAtomicDto() else it.toDto()
        } }

        if (result == null)
            Result.failure(InternalDbException("Error while trying to gel all Institutionen from db."))
        else
            Result.success(Serializer.stable.toJson(InstitutionDto.serializer().list, result).toString())
    }

    fun getById(id: UUID, atomic: Boolean = false): Result<String> = transaction {
        val result = fromTry {
            if (atomic) Institution[id].toAtomicDto() else Institution[id].toDto()
        }

        if (result == null)
            Result.failure(InstitutionIdNotValidException(""))
        else
            Result.success(mapJsonResult(result))
    }

    fun createOrUpdate(institutionDto: InstitutionDto): Result<String> = transaction {
        Institution.save(institutionDto).map { mapJsonResult(it.toDto()) }
    }

    fun delete(institutionsId: UUID): Boolean = Institution.delete(institutionsId)

    private fun mapJsonResult(result: InstitutionDto) =
        Serializer.stable.toJson(InstitutionDto.serializer(), result).toString()
}