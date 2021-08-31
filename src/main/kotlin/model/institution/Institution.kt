package model.institution

import error_handling.AddressIdNotFoundException
import error_handling.CouldNotParseUuidException
import error_handling.InstitutionIdNotValidException
import kotlinx.serialization.Serializable
import model.address.Adresse
import model.address.AdresseDto
import model.address.Adressen
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
    val bezeichnung = text("bezeichnung")
    val adress_id = reference("adress_id", Adressen)
    val email = text("email")
}

class Institution(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var bezeichnung by Institutionen.bezeichnung
    private var adresse by Adresse referencedOn Institutionen.adress_id
    private var email by Institutionen.email

    companion object : UUIDEntityClass<Institution>(Institutionen) {

        fun save(dto: InstitutionDto): Result<Institution> = transaction {
            // validate ids
            val adresseId = anyOrNull { UUID.fromString(dto.adress_id) }
                ?: return@transaction Result.failure(
                    CouldNotParseUuidException("adress_id for Institution not valid.")
                )

            // get kontakt/adresse from db
            val adresse = anyOrNull { Adresse[adresseId] }
                ?: return@transaction Result.failure(AddressIdNotFoundException("Could not find Adresse with ID: ${dto.adress_id}"))

            // matched institution
            val matchedInstitution = Institution.find {
                (Institutionen.bezeichnung eq dto.bezeichnung) and (Institutionen.adress_id eq adresseId) and (Institutionen.email eq dto.email)
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
                        veranstaltungen.addAll(Veranstaltung.find { Veranstaltungen.veranstalter_id eq it.id })
                    }

                    val berichte = mutableListOf<Bericht>()
                    veranstaltungen.forEach {
                        berichte.addAll(Bericht.find { Berichte.veranstaltung_id eq it.id})
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
        this.bezeichnung = dto.bezeichnung
        this.adresse = adresse
        this.email = dto.email
    }

    fun toDto() = InstitutionDto(
        id.value.toString(),
        bezeichnung,
        adresse.id.value.toString(),
        email
    )

    fun toAtomicDto() = InstitutionDto(
        id.value.toString(),
        bezeichnung,
        adresse.id.value.toString(),
        email,
        adresse.toAtomicDto()
    )
}

@Serializable
data class InstitutionDto(
    val uuid: String?,
    val bezeichnung: String,
    val adress_id: String,
    val email: String,
    val adresse: AdresseDto? = null
)
