package model.veranstalter

import error_handling.CouldNotParseUuidException
import error_handling.InstitutionIdNotValidException
import error_handling.TooManyHostsException
import error_handling.HostIdNotValidException
import kotlinx.serialization.Serializable
import model.institution.Institution
import model.institution.InstitutionDto
import model.institution.Institutionen
import model.schule.Schule
import model.schule.SchuleDto
import model.schule.Schulen
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object VeranstalterTable : UUIDTable() {
    val university_id = reference("hochschul_id", Schulen).nullable()
    val institution_id = reference("institution_id", Institutionen).nullable()
}

class Veranstalter(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var university by Schule optionalReferencedOn VeranstalterTable.university_id
    private var institution by Institution optionalReferencedOn VeranstalterTable.institution_id

    companion object : UUIDEntityClass<Veranstalter>(VeranstalterTable) {
        fun save(dto: VeranstalterDto): Result<Veranstalter> = transaction {
            // get validate ids
            val hochschulId = anyOrNull { UUID.fromString(dto.university_id) }
            val institutionId = anyOrNull { UUID.fromString(dto.institution_id) }

            if (hochschulId == null && institutionId == null) return@transaction Result.failure(
                CouldNotParseUuidException("institution_id or hochschul_id for Veranstalter not valid.")
            )

            // get objects by ids
            val hochschule = anyOrNull { Schule[hochschulId!!] }
            val institution = anyOrNull { Institution[institutionId!!] }

            if (hochschule == null && institution == null) return@transaction Result.failure(
                InstitutionIdNotValidException("UUID for hochschule or institution is not valid")
            )

            if (hochschule != null && institution != null) return@transaction Result.failure(
                TooManyHostsException("There should only be one uuid for either hochschule or institution")
            )

            // find matched veranstalter
            val matchedVeranstalter = Veranstalter.find {
                (VeranstalterTable.university_id eq hochschulId) or (VeranstalterTable.institution_id eq institutionId)
            }.firstOrNull()

            // update/create
            val veranstalter = when {
                dto.uuid != null -> {
                    val uuid = anyOrNull { UUID.fromString(dto.uuid) }
                        ?: return@transaction Result.failure(CouldNotParseUuidException("UUID for Veranstalter is not a valid uuid."))
                    val old = anyOrNull { Veranstalter[uuid] }
                        ?: return@transaction Result.failure(HostIdNotValidException("UUID ($uuid) is not a valid ID for Veranstalter"))
                    old.update(hochschule, institution)

                    Veranstalter[uuid]
                }
                matchedVeranstalter != null -> matchedVeranstalter
                else -> new { update(hochschule, institution) }
            }

            Result.success(veranstalter)
        }

        fun delete(veranstalterId: UUID): Boolean {
            val result = anyOrNull {
                transaction {
                    VeranstalterTable.deleteWhere { VeranstalterTable.id eq veranstalterId }
                }
            }

            return result != null
        }
    }

    private fun update(hochschule: Schule?, institution: Institution?) {
        this.university = hochschule
        this.institution = institution
    }

    fun toDto() = VeranstalterDto(
        id.value.toString(),
        university?.id?.value?.toString(),
        institution?.id?.value?.toString()
    )

    fun toAtomicDto() = VeranstalterDto(
        id.value.toString(),
        university?.id?.value?.toString(),
        institution?.id?.value?.toString(),
        university?.toDto(),
        institution?.toDto()
    )
}

@Serializable
data class VeranstalterDto(
    val uuid: String?,
    val university_id: String? = null,
    val institution_id: String? = null,
    val university: SchuleDto? = null,
    val institution: InstitutionDto? = null
)