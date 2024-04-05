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

object KontakteHochschule : UUIDTable() {
    val name = text("name")
}

class KontaktHochschule(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var name by KontakteHochschule.name

    companion object : UUIDEntityClass<KontaktHochschule>(KontakteHochschule) {
        fun save(dto: KontaktHochschuleDto): Result<KontaktHochschule> = transaction {
            val matchedKontakte = KontaktHochschule.find {
                (KontakteHochschule.name eq dto.name)
            }
            val matchedKontakt = if (matchedKontakte.empty()) null else matchedKontakte.first()

            val kontakt: KontaktHochschule = when {
                dto.id != null -> {
                    val uuid = anyOrNull { UUID.fromString(dto.id) }
                        ?: return@transaction Result.failure(CouldNotParseUuidException("Could not parse UUID: ${dto.id}"))
                    val currentKontakt = anyOrNull { KontaktHochschule[uuid] }
                        ?: return@transaction Result.failure(Exception("Could not find Kontakt with ID: $uuid"))
                    currentKontakt.update(dto)
                    KontaktHochschule[uuid]
                }

                matchedKontakt != null -> matchedKontakt
                else -> KontaktHochschule.new { update(dto) }
            }
            Result.success(kontakt)
        }
    }

    private fun update(dto: KontaktHochschuleDto) {
        this.name = dto.name
    }

    fun toDto() = KontaktHochschuleDto(id.value.toString(), name)
}

@Serializable
data class KontaktHochschuleDto(
    val id: String? = null,
    val name: String
)