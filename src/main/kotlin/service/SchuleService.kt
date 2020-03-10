package service

import database.Schule
import dto.SchuleDto
import org.jetbrains.exposed.sql.transactions.transaction

object SchuleService {

    fun getAll(): List<SchuleDto> = transaction {
        Schule.all().map { it.toDto() }
    }

//    fun update(schuleDto: SchuleDto): SchuleDto {
//        val result = Schule.save(schuleDto)?.toDto()
//
//        return result
//    }

}