package adresse

import adresse.table.Adressen
import error_handling.OrtIdNotFoundException
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import ort.Ort
import utilty.fromTry

class Adresse(id: EntityID<Int>) : IntEntity(id) {
    var strasse by Adressen.strasse
    var hausnummer by Adressen.hausnummer
    var ort by Ort referencedOn Adressen.ort

    companion object : IntEntityClass<Adresse>(Adressen) {
        /**
         * persist in db
         */
        fun save(dto: AdresseDto): Result<Adresse> = transaction {
            val ort = fromTry { Ort[dto.ort_id] }
                ?: return@transaction Result.failure<Adresse>(OrtIdNotFoundException("Could't update Adresse due to wrong "))

            val matchedAdressen = Adresse.find {
                (Adressen.ort eq dto.ort_id)
                    .and(Adressen.strasse eq dto.strasse)
                    .and(Adressen.hausnummer eq dto.hausnummer)
            }
            val matchedAdresse = if (matchedAdressen.empty()) null else matchedAdressen.first()

            val adresse = when {
                dto.adress_id != null -> {
                    val old = Adresse[dto.adress_id]
                    old.update(dto, ort)
                    Adresse[dto.adress_id]
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

    fun toDto() = AdresseDto(id.value, strasse, hausnummer, ort.id.value)
}
