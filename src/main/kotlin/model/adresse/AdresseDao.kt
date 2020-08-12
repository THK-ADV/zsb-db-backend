package model.adresse

import org.jetbrains.exposed.sql.transactions.transaction
import utilty.Serializer
import java.util.*

object AdresseDao {
    fun getAll(): List<AdresseDto> = transaction{
        Adresse.all().map { it.toDto() }
    }

    fun getById(id: UUID): AdresseDto = transaction {
        Adresse[id].toDto()
    }

    fun createOrUpdate(adresseDto: AdresseDto): Result<String> = transaction {
        Adresse.save(adresseDto).map { Serializer.stable.toJson(AdresseDto.serializer(), it.toDto()).toString() }
    }

    fun getAllAtomic(): List<AdresseDto> = transaction {
        Adresse.all().map {
            val dto = it.toDto()
            dto.ort = it.ort.toDto()

            dto
        }
    }
}
