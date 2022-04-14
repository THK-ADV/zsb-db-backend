package model.ort

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object OrtDao {
    fun getAll(): List<OrtDto> = transaction {
        Ort.all().map { it.toDto() }
    }

    fun getById(id: UUID): OrtDto = transaction {
        Ort[id].toDto()
    }

    fun createOrUpdate(ortDto: OrtDto): Result<String> = transaction {
        Ort.save(ortDto).map { Json.encodeToString(it.toDto()) }
    }
}
