package adresse

import org.jetbrains.exposed.sql.transactions.transaction
import utilty.Serializer

object AdresseService {
    fun getAll(): List<AdresseDto> = transaction{
        Adresse.all().map { it.toDto() }
    }

    fun getById(id: Int): AdresseDto = transaction {
        Adresse[id].toDto()
    }

    fun createOrUpdate(adresseDto: AdresseDto): Result<String> = transaction {
        Adresse.save(adresseDto).map { Serializer.stable.toJson(AdresseDto.serializer(), it.toDto()).toString() }
    }
}
