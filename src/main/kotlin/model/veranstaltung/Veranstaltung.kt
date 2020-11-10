package model.veranstaltung

import error_handling.CouldNotParseUuidException
import error_handling.UuidNotFound
import kotlinx.serialization.Serializable
import model.bericht.Berichte
import model.veranstalter.Veranstalter
import model.veranstalter.VeranstalterDto
import model.veranstalter.VeranstalterTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object Veranstaltungen : UUIDTable() {
    val bezeichnung = text("bezeichnung")
    val veranstalter_id = reference("veranstalter_id", VeranstalterTable)
    val kategorie = text("kategorie")
    val thema = text("thema")
    val datum = text("datum")
    val anzahlSus = text("anzahl_sus")
    val stufe = text("stufe")
    val ablauf = text("ablauf_und_bewertung")
    val durchlaeufe = text("anzahl_der_durchlaeufe")
    val ansprechpartner = text("ansprechpartner")
}

class Veranstaltung(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var bezeichnung by Veranstaltungen.bezeichnung
    private var veranstalter by Veranstalter referencedOn Veranstaltungen.veranstalter_id
    private var kategorie by Veranstaltungen.kategorie
    private var thema by Veranstaltungen.thema
    private var datum by Veranstaltungen.datum
    private var anzahlSus by Veranstaltungen.anzahlSus
    private var stufe by Veranstaltungen.stufe
    private var ablauf by Veranstaltungen.ablauf
    private var durchlaeufe by Veranstaltungen.durchlaeufe
    private var ansprechpartner by Veranstaltungen.ansprechpartner

    companion object : UUIDEntityClass<Veranstaltung>(Veranstaltungen) {
        fun save(dto: VeranstaltungDto): Result<Veranstaltung> = transaction {
            // validate ids
            val veranstalterId = anyOrNull { UUID.fromString(dto.veranstalter_id) }
                ?: return@transaction Result.failure(
                    CouldNotParseUuidException("veranstalter_id for Veranstaltung is not valid.")
                )

            // get related objects from db
            val veranstalter = anyOrNull { Veranstalter[veranstalterId] }
                ?: return@transaction Result.failure(UuidNotFound("Could not find Veranstalter with ID: $veranstalterId"))

            // finding a matched Veranstaltung is skipped here due to the unique

            // update/create Veranstaltung
            val veranstaltung = if (dto.uuid != null) {
                val uuid = anyOrNull { UUID.fromString(dto.uuid) }
                    ?: return@transaction Result.failure(CouldNotParseUuidException("UUID for Veranstaltung is not valid."))
                val old = anyOrNull { Veranstaltung[uuid] }
                    ?: return@transaction Result.failure(UuidNotFound("Couldn't find Veranstaltung with UUID: $uuid"))

                // update and safe
                old.update(dto, veranstalter)
                Veranstaltung[uuid]
            } else {
                new { update(dto, veranstalter) }
            }

            // return result
            Result.success(veranstaltung)
        }

        /**
         * Delete veranstaltung with [veranstaltungId] and all attached [Berichte]
         */
        fun delete(veranstaltungId: UUID): Boolean {
            val result = anyOrNull {
                transaction {
                    Berichte.deleteWhere { Berichte.veranstaltung_id eq veranstaltungId }
                    Veranstaltungen.deleteWhere { Veranstaltungen.id eq veranstaltungId }
                }
            }

            return result != null
        }
    }

    private fun update(dto: VeranstaltungDto, veranstalter: Veranstalter) {
        this.bezeichnung = dto.bezeichnung
        this.veranstalter = veranstalter
        this.kategorie = transformMultiSelect(dto.kategorie)
        this.thema = dto.thema
        this.datum = dto.datum
        this.anzahlSus = dto.anzahlSus
        this.stufe = transformMultiSelect(dto.stufe)
        this.ablauf = dto.ablauf
        this.durchlaeufe = dto.durchlaeufe
        this.ansprechpartner = dto.ansprechpartner
    }

    fun toDto() = VeranstaltungDto(
        id.value.toString(),
        bezeichnung,
        veranstalter.id.value.toString(),
        transformMultiSelect(kategorie),
        thema,
        datum,
        anzahlSus,
        transformMultiSelect(stufe),
        ablauf,
        durchlaeufe,
        ansprechpartner
    )

    fun toAtomicDto() = VeranstaltungDto(
        id.value.toString(),
        bezeichnung,
        veranstalter.id.value.toString(),
        transformMultiSelect(kategorie),
        thema,
        datum,
        anzahlSus,
        transformMultiSelect(stufe),
        ablauf,
        durchlaeufe,
        ansprechpartner,
        veranstalter.toDto()
    )

    private val separator = ":"

    private fun transformMultiSelect(ids: List<Int>) = ids.fold("") {acc, it -> acc + separator + it}
    private fun transformMultiSelect(ids: String): List<Int> {
        val list = mutableListOf<Int?>()
        ids.split(separator).forEach { list.add(it.toIntOrNull()) }
        return list.filterNotNull()
    }
}

@Serializable
data class VeranstaltungDto(
    val uuid: String?,
    val bezeichnung: String,
    val veranstalter_id: String,
    val kategorie: List<Int>,
    val thema: String,
    val datum: String,
    val anzahlSus: String,
    val stufe: List<Int>,
    val ablauf: String,
    val durchlaeufe: String,
    val ansprechpartner: String,
    val veranstalter: VeranstalterDto? = null
)