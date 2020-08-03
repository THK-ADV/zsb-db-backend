package ort

import org.jetbrains.exposed.sql.transactions.transaction
import utilty.Serializer
import java.util.*

object OrtDao {
    fun getAll(): List<OrtDto> = transaction {
        Ort.all().map { it.toDto() }
    }

    fun getById(id: UUID): OrtDto = transaction {
        Ort[id].toDto()
    }

    fun createOrUpdate(ortDto: OrtDto): Result<String> = transaction {
        Ort.save(ortDto).map {
            Serializer.stable.toJson(OrtDto.serializer(), it.toDto()).toString()
        }
    }
}