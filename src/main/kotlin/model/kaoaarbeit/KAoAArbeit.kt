package model.kaoaarbeit

import error_handling.SchoolIdNotFoundException
import kotlinx.serialization.Serializable
import model.schule.Schule
import model.schule.SchuleDto
import model.schule.Schulen
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object KAoAArbeiten : UUIDTable() {
    val name = text("name")
    val content = text("inhalt")
    val school = reference("schule_id", Schulen)
}

class KAoAArbeit(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var name by KAoAArbeiten.name
    private var content by KAoAArbeiten.content
    private var school by Schule referencedOn KAoAArbeiten.school

    companion object : UUIDEntityClass<KAoAArbeit>(KAoAArbeiten) {
        fun save(dto: KAoAArbeitDto): Result<KAoAArbeit> = transaction {
            val schule = anyOrNull { Schule[UUID.fromString(dto.school_id)] }
                ?: return@transaction Result.failure(SchoolIdNotFoundException("Could not find School with ID: ${dto.school_id}"))
            if (dto.id != null) {
                val id = UUID.fromString(dto.id)
                val old = KAoAArbeit[id]
                old.update(dto, schule)
                return@transaction Result.success(KAoAArbeit[id])
            }

            val kAoAArbeit: KAoAArbeit = KAoAArbeit.new { update(dto, schule) }
            Result.success(kAoAArbeit)
        }
    }

    private fun update(dto: KAoAArbeitDto, school: Schule) {
        this.name = dto.name
        this.content = dto.content
        this.school = school
    }

    fun toDto() = KAoAArbeitDto(id.value.toString(), name, content, school.id.value.toString(), null)
    fun toAtomicDto() = KAoAArbeitDto(id.value.toString(), name, content, school.id.value.toString(), school.toDto())
}

@Serializable
data class KAoAArbeitDto(
    val id: String? = null,
    val name: String,
    val content: String,
    val school_id: String,
    val school: SchuleDto? = null
)