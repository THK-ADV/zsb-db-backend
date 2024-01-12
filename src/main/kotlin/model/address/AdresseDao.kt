package model.address

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object AdresseDao {
    fun getAll(): List<AdresseDto> = transaction {
        Adresse.all().map { it.toDto() }
    }

    fun getById(id: UUID): AdresseDto = transaction {
        Adresse[id].toDto()
    }

    fun createOrUpdate(adresseDto: AdresseDto): Result<String> = transaction {
        Adresse.save(adresseDto).map { Json.encodeToString(it.toDto()) }
    }
}
