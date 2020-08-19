package model.ort

import error_handling.CouldNotParseUuidException
import error_handling.SchuleIdNotFoundException
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object Orte : UUIDTable() {
    val regierungsbezirk = text("regierungsbezirk")
    val kreis = text("kreis")
    val plz = integer("plz")
    val bezeichnung = text("bezeichnung")
}

class Ort(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var plz by Orte.plz
    private var bezeichnung by Orte.bezeichnung
    private var kreis by Orte.kreis
    private var regierungsbezirk by Orte.regierungsbezirk

    companion object : UUIDEntityClass<Ort>(Orte) {
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
                    // parse UUID
                    val uuid = anyOrNull { UUID.fromString(dto.ort_id) }
                        ?: return@transaction Result.failure(CouldNotParseUuidException("Could parse UUID: ${dto.ort_id}"))

                    // fetch current Ort
                    val currentOrt = anyOrNull { Ort[uuid] }
                        ?: return@transaction Result.failure(SchuleIdNotFoundException("Could not find Ort with ID: $uuid"))

                    // update Ort
                    currentOrt.update(dto)

                    // fetch updated Ort
                    Ort[uuid]
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

    fun toDto() = OrtDto(id.value.toString(), plz, bezeichnung, kreis, regierungsbezirk)
}

@Serializable
data class OrtDto(
    val ort_id: String? = null,
    val plz: Int,
    val bezeichnung: String,
    val kreis: String,
    val regierungsbezirk: String
)