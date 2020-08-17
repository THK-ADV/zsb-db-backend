package model.veranstaltung

import error_handling.CouldNotParseUuidException
import error_handling.UuidNotFound
import kotlinx.serialization.Serializable
import model.kontakt.Kontakt
import model.kontakt.KontaktDto
import model.kontakt.Kontakte
import model.veranstalter.Veranstalter
import model.veranstalter.VeranstalterDto
import model.veranstalter.VeranstalterTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.fromTry
import java.util.*

object Veranstaltungen : UUIDTable() {
    val bezeichnung = text("bezeichnung")
    val veranstalter_id = reference("veranstalter_id", VeranstalterTable)
    val kategorie = integer("kategorie")
    val thema = text("thema")
    val vortragsart = integer("vortragsart").nullable() // only available if kategorie == vortrag
    val datum = text("datum") // TODO should be format date...
    val kontaktperson = reference("kontaktperson", Kontakte)
    val anzahlSus = integer("anzahl_sus")
    val stufe = integer("stufe")
    val ablauf = text("ablauf_und_bewertung")
    val durchlaeufe = text("anzahl_der_durchlaeufe")
}

class Veranstaltung(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    var bezeichnung by Veranstaltungen.bezeichnung
    var veranstalter by Veranstalter referencedOn Veranstaltungen.veranstalter_id
    var kategorie by Veranstaltungen.kategorie
    var thema by Veranstaltungen.thema
    var vortragsart by Veranstaltungen.vortragsart
    var datum by Veranstaltungen.datum
    var kontaktperson by Kontakt referencedOn Veranstaltungen.kontaktperson
    var anzahlSus by Veranstaltungen.anzahlSus
    var stufe by Veranstaltungen.stufe
    var ablauf by Veranstaltungen.ablauf
    var durchlaeufe by Veranstaltungen.durchlaeufe

    companion object : UUIDEntityClass<Veranstaltung>(Veranstaltungen) {
        fun save(dto: VeranstaltungDto): Result<Veranstaltung> = transaction {
            // validate ids
            val veranstalterId = fromTry { UUID.fromString(dto.veranstalter_id) }
                ?: return@transaction Result.failure(
                    CouldNotParseUuidException("veranstalter_id for Veranstaltung is not valid.")
                )
            val kontaktpersonId = fromTry { UUID.fromString(dto.kontaktperson_id) }
                ?: return@transaction Result.failure(
                    CouldNotParseUuidException("kontaktperson_id for Veranstaltung is not valid.")
                )

            // get related objects from db
            val veranstalter = fromTry { Veranstalter[veranstalterId] }
                ?: return@transaction Result.failure(UuidNotFound("Could not find Veranstalter with ID: $veranstalterId"))
            val kontaktperson = fromTry { Kontakt[kontaktpersonId] }
                ?: return@transaction Result.failure(UuidNotFound("Could not find Kontakt(Person) with ID: $kontaktpersonId"))

            // finding a matched Veranstaltung is skipped here due to the unique

            // update/create Veranstaltung
            val veranstaltung = if (dto.uuid != null) {
                val uuid = fromTry { UUID.fromString(dto.uuid) }
                    ?: return@transaction Result.failure(CouldNotParseUuidException("UUID for Veranstaltung is not valid."))
                val old = fromTry { Veranstaltung[uuid] }
                    ?: return@transaction Result.failure(UuidNotFound("Couldn't find Veranstaltung with UUID: $uuid"))

                // update and safe
                old.update(dto, veranstalter, kontaktperson)
                Veranstaltung[uuid]
            } else {
                new { update(dto, veranstalter, kontaktperson) }
            }

            // return result
            Result.success(veranstaltung)
        }

        fun delete(veranstaltungId: UUID): Boolean {
            val result = fromTry {
                transaction {
                    Veranstaltungen.deleteWhere { Veranstaltungen.id eq veranstaltungId }
                }
            }

            return result != null
        }
    }

    private fun update(dto: VeranstaltungDto, veranstalter: Veranstalter, kontaktperson: Kontakt) {
        this.veranstalter = veranstalter
        this.kategorie = dto.kategorie
        this.thema = dto.thema
        this.vortragsart = dto.vortragsart
        this.datum = dto.datum
        this.kontaktperson = kontaktperson
        this.anzahlSus = dto.anzahlSus
        this.stufe = dto.stufe
        this.ablauf = dto.ablauf
        this.durchlaeufe = dto.durchlaeufe
    }

    fun toDto() = VeranstaltungDto(
        id.value.toString(),
        bezeichnung,
        veranstalter.id.value.toString(),
        kategorie,
        thema,
        vortragsart,
        datum,
        kontaktperson.id.value.toString(),
        anzahlSus,
        stufe,
        ablauf,
        durchlaeufe
    )

    fun toAtomicDto() = VeranstaltungDto(
        id.value.toString(),
        bezeichnung,
        veranstalter.id.value.toString(),
        kategorie,
        thema,
        vortragsart,
        datum,
        kontaktperson.id.value.toString(),
        anzahlSus,
        stufe,
        ablauf,
        durchlaeufe,
        veranstalter.toDto(),
        kontaktperson.toDto()
    )
}

@Serializable
data class VeranstaltungDto(
    val uuid: String?,
    val bezeichnung: String,
    val veranstalter_id: String,
    val kategorie: Int,
    val thema: String,
    val vortragsart: Int?,
    val datum: String,
    val kontaktperson_id: String,
    val anzahlSus: Int,
    val stufe: Int,
    val ablauf: String,
    val durchlaeufe: String,
    val veranstalter: VeranstalterDto? = null,
    val kontaktperson: KontaktDto? = null
)