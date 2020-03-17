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
        fun save(dto: OrtDto): Result<Ort> {
            val ort = transaction {
                new(dto.ort_id) {
                    this.plz = dto.plz
                    this.bezeichnung = dto.bezeichnung
                }
            }

            return Result.success(ort)
        }

    }

    fun toDto() = OrtDto(id.value, plz, bezeichnung)
}