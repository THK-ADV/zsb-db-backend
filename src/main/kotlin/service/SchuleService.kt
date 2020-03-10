package service

import database.Schule
import dto.SchuleDto
import org.jetbrains.exposed.sql.transactions.transaction

object SchuleService {

    fun getAll(): List<SchuleDto> = transaction {
        Schule.all().map { it.toDto() }
    }

        return result
    }
}