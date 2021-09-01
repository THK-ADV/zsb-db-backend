package model.institution

import error_handling.AdressIdNotFoundException
import error_handling.CouldNotParseUuidException
import error_handling.InstitutionIdNotValidException
import kotlinx.serialization.Serializable
import model.adresse.Adresse
import model.adresse.AdresseDto
import model.adresse.Adressen
import model.bericht.Bericht
import model.bericht.Berichte
import model.veranstalter.Veranstalter
import model.veranstalter.VeranstalterTable
import model.veranstaltung.Veranstaltung
import model.veranstaltung.Veranstaltungen
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object Institutionen : UUIDTable() {
    val designation = text("bezeichnung")
    val address_id = reference("adress_id", Adressen)
    val email = text("email")
}

class Institution(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var designation by Institutionen.designation
    private var address by Adresse referencedOn Institutionen.address_id
    private var email by Institutionen.email

    companion object : UUIDEntityClass<Institution>(Institutionen) {

        fun save(dto: InstitutionDto): Result<Institution> = transaction {
            // validate ids
            val adresseId = anyOrNull { UUID.fromString(dto.address_id) }
                ?: return@transaction Result.failure(
                    CouldNotParseUuidException("adress_id for Institution not valid.")
                )

            // get kontakt/adresse from db
            val adresse = anyOrNull { Adresse[adresseId] }
                ?: return@transaction Result.failure(AdressIdNotFoundException("Could not find Adresse with ID: ${dto.address_id}"))

            // matched institution
            val matchedInstitution = Institution.find {
                (Institutionen.designation eq dto.designation) and (Institutionen.address_id eq adresseId) and (Institutionen.email eq dto.email)
            }.firstOrNull()

            val institution = when {
                dto.uuid != null -> {
                    val uuid = anyOrNull { UUID.fromString(dto.uuid) }
                        ?: return@transaction Result.failure(CouldNotParseUuidException("UUID for Institution not valid."))
                    val old = anyOrNull { Institution[uuid] }
                        ?: return@transaction Result.failure(InstitutionIdNotValidException("UUID ($uuid) is not a valid ID for Institution"))

                    old.update(dto, adresse)

                    Institution[uuid]
                }
                matchedInstitution != null -> matchedInstitution
                else -> new { update(dto, adresse) }
            }

            Result.success(institution)
        }

        fun delete(institutionsId: UUID): Boolean {
            val result = anyOrNull {
                transaction {
                    val institution = Institution.find { Institutionen.id eq institutionsId }.first()
                    val veranstalterList = Veranstalter.find { VeranstalterTable.institution_id eq institution.id }.toList()

                    val veranstaltungen = mutableListOf<Veranstaltung>()
                    veranstalterList.forEach {
                        veranstaltungen.addAll(Veranstaltung.find { Veranstaltungen.host_id eq it.id })
                    }

                    val berichte = mutableListOf<Bericht>()
                    veranstaltungen.forEach {
                        berichte.addAll(Bericht.find { Berichte.event_id eq it.id})
                    }

                    berichte.forEach { it.delete() }
                    veranstaltungen.forEach { it.delete() }
                    veranstalterList.forEach { it.delete() }
                    institution.delete()
                }
            }

            return result != null
        }
    }

    private fun update(dto: InstitutionDto, adresse: Adresse) {
        this.designation = dto.designation
        this.address = adresse
        this.email = dto.email
    }

    fun toDto() = InstitutionDto(
        id.value.toString(),
        designation,
        address.id.value.toString(),
        email
    )

    fun toAtomicDto() = InstitutionDto(
        id.value.toString(),
        designation,
        address.id.value.toString(),
        email,
        address.toAtomicDto()
    )
}

@Serializable
data class InstitutionDto(
    val uuid: String?,
    val designation: String,
    val address_id: String,
    val email: String,
    val address: AdresseDto? = null
)
