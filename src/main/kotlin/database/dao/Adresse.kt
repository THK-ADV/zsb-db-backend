package database.dao

import database.Adressen
import dto.AdresseDto
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class Adresse(id: EntityID<Int>) : IntEntity(id) {
    var strasse by Adressen.strasse
    var hausnummer by Adressen.hausnummer
    var ort by Ort referencedOn Adressen.ort

    companion object : IntEntityClass<Adresse>(Adressen) {
        /**
         * persist in db
         */
        fun save(dto: AdresseDto): Result<Adresse> = transaction {
            val ort = Ort[dto.ort_id] // TODO catch exceptions? EntityNotFoundException


            val adresse = if (dto.adress_id == null) new(dto.adress_id) {
                update(dto, ort)
            } else {
                val old = Adresse[dto.adress_id]
                old.update(dto, ort)
                Adresse[dto.adress_id]
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
