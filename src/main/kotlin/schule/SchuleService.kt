package schule

import org.jetbrains.exposed.sql.transactions.transaction
import utilty.Serializer

object SchuleService {
    fun getAll(): List<SchuleDto> = transaction {
        Schule.all().map { it.toDto() }
    }

    fun getById(id: Int): SchuleDto = transaction{
        Schule[id].toDto()
    }

    fun createOrUpdate(schuleDto: SchuleDto): Result<String> = transaction {
        Schule.save(schuleDto).map {
            val dto = it.toDto()
            val json = Serializer.stable.toJson(SchuleDto.serializer(), dto)

            json.toString()
        }
    }
}