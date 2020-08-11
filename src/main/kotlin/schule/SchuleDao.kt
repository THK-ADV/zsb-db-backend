package schule

import org.jetbrains.exposed.sql.transactions.transaction
import utilty.Serializer
import java.util.*

object SchuleDao {
    fun getAll(): List<SchuleDto> = transaction {
        Schule.all().map { it.toDto() }
    }

    fun getAllAtomic(): List<SchuleDto> = transaction {
        Schule.all().map {
            val dto = it.toDto()
            dto.adresse = it.adresse.toDto()
            dto.ort = it.adresse.ort.toDto()
            dto.kontakte = it.kontakte.map { kontakt -> kontakt.toDto() }

            dto
        }
    }

    fun getById(id: UUID): SchuleDto = transaction {
        Schule[id].toDto()
    }

    fun getByIdAtomic(id: UUID): SchuleDto = transaction {
        val entity = Schule[id]
        val dto = entity.toDto()
        dto.adresse = entity.adresse.toDto()
        dto.ort = entity.adresse.ort.toDto()
        dto.kontakte = entity.kontakte.map { it.toDto() }

        dto
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