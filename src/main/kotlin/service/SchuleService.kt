package service

import database.Schule
import dto.SchuleDto
import org.jetbrains.exposed.sql.transactions.transaction

object SchuleService {

    fun getAll(): List<SchuleDto> {
        val result = mutableListOf<SchuleDto>()

        transaction {
            Schule.all().forEach { entity ->
                result.add(SchuleDto.convert(entity))
            }
        }

        return result
    }
}