package model.address

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object AdresseDao {
    fun getAll(atomic: Boolean = false): List<AdresseDto> = transaction {
        Adresse.all().map { if (atomic) it.toAtomicDto() else it.toDto() }
    }

    fun getById(id: UUID, atomic: Boolean = false): AdresseDto = transaction {
        if (atomic) Adresse[id].toAtomicDto() else Adresse[id].toDto()
    }

    fun createOrUpdate(adresseDto: AdresseDto): Result<String> = transaction {
        Adresse.save(adresseDto).map { Json.encodeToString(it.toDto()) }
    }
}
