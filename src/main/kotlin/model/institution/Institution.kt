package model.institution

import error_handling.AdressIdNotFoundException
import error_handling.CouldNotParseUuidException
import error_handling.InstitutionIdNotValidException
import error_handling.KontaktIdNotValidException
import kotlinx.serialization.Serializable
import model.adresse.Adresse
import model.adresse.AdresseDto
import model.adresse.Adressen
import model.kontakt.Kontakt
import model.kontakt.KontaktDto
import model.kontakt.Kontakte
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.fromTry
import java.util.*

object Institutionen : UUIDTable() {
    val bezeichnung = text("bezeichnung")
    val adress_id = reference("adress_id", Adressen)
    val ansprechpartner_id = reference("ansprechpartner_id", Kontakte)
}

class Institution(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    var bezeichnung by Institutionen.bezeichnung
    var adresse by Adresse referencedOn Institutionen.adress_id
    var ansprechpartner by Kontakt referencedOn Institutionen.ansprechpartner_id

    companion object : UUIDEntityClass<Institution>(Institutionen) {

        fun save(dto: InstitutionDto): Result<Institution> = transaction {
            // validate ids
            val ansprechpartnerId = fromTry { UUID.fromString(dto.ansprechpartner_id) }
                ?: return@transaction Result.failure(
                    CouldNotParseUuidException("ansprechpartner_id for Institution not valid.")
                )
            val adresseId = fromTry { UUID.fromString(dto.adress_id) }
                ?: return@transaction Result.failure(
                    CouldNotParseUuidException("adress_id for Institution not valid.")
                )

            // get kontakt/adresse from db
            val adresse = fromTry { Adresse[adresseId] }
                ?: return@transaction Result.failure(AdressIdNotFoundException("Could not find Adresse with ID: ${dto.adress_id}"))
            val ansprechpartner = fromTry { Kontakt[ansprechpartnerId] }
                ?: return@transaction Result.failure(KontaktIdNotValidException("Could not find Kontakt with ID: ${dto.ansprechpartner_id}"))

            // matched institution
            val matchedInstitution = Institution.find {
                (Institutionen.bezeichnung eq dto.bezeichnung) and (Institutionen.adress_id eq adresseId) and (Institutionen.ansprechpartner_id eq ansprechpartnerId)
            }.firstOrNull()

            val institution = when {
                dto.uuid != null -> {
                    val uuid = fromTry { UUID.fromString(dto.uuid) }
                        ?: return@transaction Result.failure(CouldNotParseUuidException("UUID for Institution not valid."))
                    val old = fromTry { Institution[uuid] }
                        ?: return@transaction Result.failure(InstitutionIdNotValidException("UUID ($uuid) is not a valid ID for Institution"))

                    old.update(dto, adresse, ansprechpartner)

                    Institution[uuid]
                }
                matchedInstitution != null -> matchedInstitution
                else -> new { update(dto, adresse, ansprechpartner) }
            }

            Result.success(institution)
        }

        fun delete(institutionsId: UUID): Boolean {
            val result = fromTry { transaction {
                Institutionen.deleteWhere { Institutionen.id eq institutionsId }
            } }

            return result != null
        }
    }

    private fun update(dto: InstitutionDto, adresse: Adresse, ansprechpartner: Kontakt) {
        this.bezeichnung = dto.bezeichnung
        this.adresse = adresse
        this.ansprechpartner = ansprechpartner
    }

    fun toDto() = InstitutionDto(
        id.value.toString(),
        bezeichnung,
        adresse.id.value.toString(),
        ansprechpartner.id.value.toString()
    )

    fun toAtomicDto() = InstitutionDto(
        id.value.toString(),
        bezeichnung,
        adresse.id.value.toString(),
        ansprechpartner.id.value.toString(),
        adresse.toDto(),
        ansprechpartner.toDto()
    )
}

@Serializable
class InstitutionDto(
    val uuid: String?,
    val bezeichnung: String,
    val adress_id: String,
    val ansprechpartner_id: String,
    val adresse: AdresseDto? = null,
    val ansprechpartner: KontaktDto? = null
)
