package model.termin.kontakte

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction

object KontaktHochschuleDao {
    fun getAll(): List<KontaktHochschuleDto> = transaction {
        KontaktHochschule.all().map { it.toDto() }
    }

    fun create(kontaktHochschuleDto: KontaktHochschuleDto): Result<String> = transaction {
        KontaktHochschule.save(kontaktHochschuleDto).map { Json.encodeToString(it.toDto()) }
    }
}