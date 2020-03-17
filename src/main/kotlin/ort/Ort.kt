package ort

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import ort.table.Orte

class Ort(id: EntityID<Int>) : IntEntity(id) {
    var plz by Orte.plz
    var bezeichnung by Orte.bezeichnung

    companion object : IntEntityClass<Ort>(Orte) {
        /**
         * persist in db
         */
        fun save(dto: OrtDto): Result<Ort> = transaction {
            val ort: Ort = if (dto.ort_id == null) new {
                update(dto)
            } else {
                val old = Ort[dto.ort_id]
                old.update(dto)

                Ort[dto.ort_id]
            }

            Result.success(ort)
        }
    }

    private fun update(dto: OrtDto) {
        this.plz = dto.plz
        this.bezeichnung = dto.bezeichnung
    }

    fun toDto() = OrtDto(id.value, plz, bezeichnung)
}