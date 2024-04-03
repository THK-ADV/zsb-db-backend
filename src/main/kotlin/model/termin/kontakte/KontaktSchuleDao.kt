package model.termin.kontakte

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction

object KontaktSchuleDao {
    fun getAll(): List<KontaktSchuleDto> = transaction {
        KontaktSchule.all().map { it.toDto() }
    }

    fun create(kontaktSchuleDto: KontaktSchuleDto): Result<String> = transaction {
        KontaktSchule.save(kontaktSchuleDto).map { Json.encodeToString(it.toDto()) }
    }
}