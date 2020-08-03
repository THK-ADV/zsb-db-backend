package ort

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

object Orte : IntIdTable() {
    val regierungsbezirk = text("regierungsbezirk")
    val kreis = text("kreis")
    val plz = integer("plz")
    val bezeichnung = text("bezeichnung")
}

class Ort(id: EntityID<Int>) : IntEntity(id) {
    var plz by Orte.plz
    var bezeichnung by Orte.bezeichnung
    var kreis by Orte.kreis
    var regierungsbezirk by Orte.regierungsbezirk

    companion object : IntEntityClass<Ort>(Orte) {
        /**
         * persist in db
         */
        fun save(dto: OrtDto): Result<Ort> = transaction {
            val matchedOrte = Ort.find {
                (Orte.bezeichnung eq dto.bezeichnung)
                    .and(Orte.plz eq dto.plz)
                    .and(Orte.kreis eq dto.kreis)
                    .and(Orte.regierungsbezirk eq dto.regierungsbezirk)
            }
            val matchedOrt = if (matchedOrte.empty()) null else matchedOrte.first()

            val ort: Ort = when {
                dto.ort_id != null -> {
                    // update ort
                    val old = Ort[dto.ort_id]
                    old.update(dto)
                    Ort[dto.ort_id]
                }
                matchedOrt != null -> matchedOrt
                else -> new { update(dto) }
            }

            Result.success(ort)
        }
    }

    private fun update(dto: OrtDto) {
        this.plz = dto.plz
        this.bezeichnung = dto.bezeichnung
        this.kreis = dto.kreis
        this.regierungsbezirk = dto.regierungsbezirk
    }

    fun toDto() = OrtDto(id.value, plz, bezeichnung, kreis, regierungsbezirk)
}