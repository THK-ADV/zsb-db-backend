package model.veranstalter

import error_handling.CouldNotParseUuidException
import error_handling.InstitutionIdNotValidException
import error_handling.SchuleIdNotFoundException
import error_handling.VeranstalterIdNotValidException
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
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.fromTry
import java.util.*

object VeranstalterTable : UUIDTable() {
    val hochschul_id = reference("hochschul_id", Schulen)
    val institution_id = reference("institution_id", Institutionen)
}

class Veranstalter(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    var hochschule by Schule referencedOn VeranstalterTable.hochschul_id
    var institution by Institution referencedOn VeranstalterTable.institution_id

    companion object : UUIDEntityClass<Veranstalter>(VeranstalterTable) {
        fun save(dto: VeranstalterDto): Result<Veranstalter> = transaction {
            // validate ids
            val hochschulId = fromTry { UUID.fromString(dto.hochschul_id) }
                ?: return@transaction Result.failure(
                    CouldNotParseUuidException("hochschul_id for Veranstalter not valid.")
                )
            val institutionId = fromTry { UUID.fromString(dto.institution_id) }
                ?: return@transaction Result.failure(
                    CouldNotParseUuidException("institution_id for Veranstalter not valid.")
                )

            // get objects by ids
            val hochschule = fromTry { Schule[hochschulId] }
                ?: return@transaction Result.failure(SchuleIdNotFoundException("Could not find Schule with ID: ${dto.hochschul_id}"))
            val institution = fromTry { Institution[institutionId] }
                ?: return@transaction Result.failure(InstitutionIdNotValidException("UUID (${dto.institution_id}) is not a valid ID for Institution"))

            // find matched veranstalter
            val matchedVeranstalter = Veranstalter.find {
                (VeranstalterTable.hochschul_id eq hochschulId) and (VeranstalterTable.institution_id eq institutionId)
            }.firstOrNull()

            // update/create
            val veranstalter = when {
                dto.uuid != null -> {
                    val uuid = fromTry { UUID.fromString(dto.uuid) }
                        ?: return@transaction Result.failure(CouldNotParseUuidException("UUID for Veranstalter is not a valid uuid."))
                    val old = fromTry { Veranstalter[uuid] }
                        ?: return@transaction Result.failure(VeranstalterIdNotValidException("UUID ($uuid) is not a valid ID for Veranstalter"))
                    old.update(dto, hochschule, institution)

                    Veranstalter[uuid]
                }
                matchedVeranstalter != null -> matchedVeranstalter
                else -> new { update(dto, hochschule, institution) }
            }

            Result.success(veranstalter)
        }

        fun delete(veranstalterId: UUID): Boolean {
            val result = fromTry {
                transaction {
                    VeranstalterTable.deleteWhere { VeranstalterTable.id eq veranstalterId }
                }
            }

            return result != null
        }
    }

    private fun update(dto: VeranstalterDto, hochschule: Schule, institution: Institution) {
        this.hochschule = hochschule
        this.institution = institution
    }

    fun toDto() = VeranstalterDto(
        id.value.toString(),
        hochschule.id.value.toString(),
        institution.id.value.toString()
    )

    fun toAtomicDto() = VeranstalterDto(
        id.value.toString(),
        hochschule.id.value.toString(),
        institution.id.value.toString(),
        hochschule.toDto(),
        institution.toDto()
    )
}

@Serializable
class VeranstalterDto(
    val uuid: String?,
    val hochschul_id: String,
    val institution_id: String,
    val hochschule: SchuleDto? = null,
    val institution: InstitutionDto? = null
)