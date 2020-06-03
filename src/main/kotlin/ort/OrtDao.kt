package ort

import org.jetbrains.exposed.sql.transactions.transaction
import utilty.Serializer

object OrtDao {
    fun getAll(): List<OrtDto> = transaction {
        Ort.all().map { it.toDto() }
    }

    fun getById(id: Int): OrtDto = transaction {
        Ort[id].toDto()
    }

    fun createOrUpdate(ortDto: OrtDto): Result<String> = transaction {
        Ort.save(ortDto).map {
            Serializer.stable.toJson(OrtDto.serializer(), it.toDto()).toString()
        }
    }
}