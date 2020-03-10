package service

import database.Schule
import dto.SchuleDto
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.jetbrains.exposed.sql.transactions.transaction

object SchuleService {
    private val serializer = Json(JsonConfiguration.Stable)

    fun getAll(): List<SchuleDto> = transaction {
        Schule.all().map { it.toDto() }
    }

    fun getById(id: Int): SchuleDto = transaction{
        Schule[id].toDto()
    }

    fun createOrUpdate(schuleDto: SchuleDto): Result<String> = transaction {
        Schule.save(schuleDto).map {
            val dto = it.toDto()
            val json = serializer.toJson(SchuleDto.serializer(), dto)

            json.toString()
        }
    }
}