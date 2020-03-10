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

    fun createOrUpdate(schuleDto: SchuleDto): Result<String> = transaction {
        Schule.save(schuleDto).map { serializer.toJson(SchuleDto.serializer(), it.toDto()).toString() }
    }
}