package model.schule

import org.jetbrains.exposed.sql.transactions.transaction
import utilty.Serializer
import java.util.*

object SchuleDao {
    fun getAll(atomic: Boolean = false): List<SchuleDto> = transaction {
        Schule.all().map { if (atomic) it.toAtomicDto() else it.toDto() }
    }

    fun getById(id: UUID, atomic: Boolean = false): SchuleDto = transaction {
        if (atomic) Schule[id].toAtomicDto() else Schule[id].toDto()
    }

    fun createOrUpdate(schuleDto: SchuleDto): Result<String> = transaction {
        Schule.save(schuleDto).map {
            val dto = it.toDto()
            val json = Serializer.stable.toJson(SchuleDto.serializer(), dto)

            json.toString()
        }
    }

    fun delete(schuleId: UUID): Boolean = Schule.delete(schuleId)
}