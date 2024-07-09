package model.schule

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import model.address.Adressen
import model.address.Adressen.entityId
import model.kontakt.Kontakte
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object SchuleDao {
    fun getAll(atomic: Boolean = false): List<SchuleDto> = transaction {
        Schule.all().map { if (atomic) it.toAtomicDto() else it.toDto() }
    }

    fun getById(id: UUID): SchuleDto = transaction {
        Schule[id].toDto()
    }

    fun getContactsByIds(ids: List<UUID>): List<Int> = transaction {
        SchulKontakte
            .innerJoin(Kontakte)
            .select { SchulKontakte.school inList ids }
            .map { it[Kontakte.feature] }
    }

    fun createOrUpdate(schuleDto: SchuleDto): Result<String> = transaction {
        Schule.save(schuleDto).map {
            val dto = it.toDto()
            val json = Json.encodeToJsonElement(dto)

            json.toString()
        }
    }

    fun delete(schuleId: UUID): Boolean = Schule.delete(schuleId)
}
