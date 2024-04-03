package model.termin.kontakte

import error_handling.CouldNotParseUuidException
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object KontakteSchule : UUIDTable() {
    val name = text("name")
}

class KontaktSchule(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var name by KontakteSchule.name

    companion object : UUIDEntityClass<KontaktSchule>(KontakteSchule) {
        fun save(dto: KontaktSchuleDto): Result<KontaktSchule> = transaction {
            val matchedKontakte = KontaktSchule.find {
                (KontakteSchule.name eq dto.name)
            }
            val matchedKontakt = if (matchedKontakte.empty()) null else matchedKontakte.first()

            val kontakt: KontaktSchule = when {
                dto.id != null -> {
                    val uuid = anyOrNull { UUID.fromString(dto.id) }
                        ?: return@transaction Result.failure(CouldNotParseUuidException("Could not parse UUID: ${dto.id}"))
                    val currentKontakt = anyOrNull { KontaktSchule[uuid] }
                        ?: return@transaction Result.failure(Exception("Could not find Kontakt with ID: $uuid"))
                    currentKontakt.update(dto)
                    KontaktSchule[uuid]
                }

                matchedKontakt != null -> matchedKontakt
                else -> new { update(dto) }
            }
            Result.success(kontakt)
        }
    }

    private fun update(dto: KontaktSchuleDto) {
        this.name = dto.name
    }

    fun toDto() = KontaktSchuleDto(id.value.toString(), name)
}

@Serializable
data class KontaktSchuleDto(
    val id: String? = null,
    val name: String
)