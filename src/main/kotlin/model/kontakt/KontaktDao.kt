package model.kontakt

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object KontaktDao {
    fun getAll(): List<KontaktDto> = transaction {
        Kontakt.all().map { it.toDto() }
    }

    fun getAllById(ids: List<String>): List<Kontakt> = transaction {
        Kontakt.all().filter { kontakt -> ids.contains(kontakt.id.value.toString()) }
    }

    fun getById(id: UUID): KontaktDto = transaction {
        Kontakt[id].toDto()
    }

    fun createOrUpdate(dto: KontaktDto): Result<String> = transaction {
        Kontakt.save(dto).map { Json.encodeToString(it.toDto()) }
    }
}
