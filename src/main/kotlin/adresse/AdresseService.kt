package adresse

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.jetbrains.exposed.sql.transactions.transaction

object AdresseService {
    private val serializer = Json(JsonConfiguration.Stable)

    fun getAll(): List<AdresseDto> = transaction{
        Adresse.all().map { it.toDto() }
    }

    fun getById(id: Int): AdresseDto = transaction {
        Adresse[id].toDto()
    }

    fun createOrUpdate(adresseDto: AdresseDto): Result<String> = transaction {
        Adresse.save(adresseDto).map { serializer.toJson(AdresseDto.serializer(), it.toDto()).toString() }
    }
}
