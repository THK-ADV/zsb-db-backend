package adresse

import error_handling.OrtIdNotFoundException
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import ort.Ort
import ort.Orte
import utilty.fromTry
import java.util.*

object Adressen : UUIDTable() {
    val strasse = varchar("strasse", 250)
    val hausnummer = varchar("hausnummer", 20)
    val ort = reference("ort", Orte)
}

class Adresse(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    var strasse by Adressen.strasse
    var hausnummer by Adressen.hausnummer
    var ort by Ort referencedOn Adressen.ort

    companion object : UUIDEntityClass<Adresse>(Adressen) {
        /**
         * persist in db
         */
        fun save(dto: AdresseDto): Result<Adresse> = transaction {
            val ortUUID = UUID.fromString(dto.ort_id)

            val ort = fromTry { Ort[ortUUID] }
                ?: return@transaction Result.failure<Adresse>(OrtIdNotFoundException("Couldn't update Adresse due to wrong Ort (ID: ${dto.ort_id})"))

            val matchedAdressen = Adresse.find {
                (Adressen.ort eq ortUUID)
                    .and(Adressen.strasse eq dto.strasse)
                    .and(Adressen.hausnummer eq dto.hausnummer)
            }
            val matchedAdresse = if (matchedAdressen.empty()) null else matchedAdressen.first()

            val adresse = when {
                dto.adress_id != null -> {
                    val uuid = UUID.fromString(dto.adress_id)
                    val old = Adresse[uuid]
                    old.update(dto, ort)
                    Adresse[uuid]
                }
                matchedAdresse != null -> matchedAdresse
                else -> new(dto.adress_id) { update(dto, ort) }
            }

            Result.success(adresse)
        }
    }

    private fun update(dto: AdresseDto, ort: Ort) {
        this.strasse = dto.strasse
        this.hausnummer = dto.hausnummer
        this.ort = ort
    }

    fun toDto() = AdresseDto(id.value.toString(), strasse, hausnummer, ort.id.value.toString())
}
