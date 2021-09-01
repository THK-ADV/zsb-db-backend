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
    val governmentDistrict = text("regierungsbezirk")
    val constituency = text("kreis")
    val postcode = integer("plz")
    val designation = text("bezeichnung")
}

class Ort(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var postcode by Orte.postcode
    private var designation by Orte.designation
    private var constituency by Orte.constituency
    private var governmentDistrict by Orte.governmentDistrict

    companion object : UUIDEntityClass<Ort>(Orte) {
        /**
         * persist in db
         */
        fun save(dto: OrtDto): Result<Ort> = transaction {
            val matchedOrte = Ort.find {
                (Orte.designation eq dto.designation)
                    .and(Orte.postcode eq dto.postcode)
                    .and(Orte.constituency eq dto.constituency)
                    .and(Orte.governmentDistrict eq dto.governmentDistrict)
            }
            val matchedOrt = if (matchedOrte.empty()) null else matchedOrte.first()

            val ort: Ort = when {
                dto.city_id != null -> {
                    // parse UUID
                    val uuid = anyOrNull { UUID.fromString(dto.city_id) }
                        ?: return@transaction Result.failure(CouldNotParseUuidException("Could parse UUID: ${dto.city_id}"))

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
        this.postcode = dto.postcode
        this.designation = dto.designation
        this.constituency = dto.constituency
        this.governmentDistrict = dto.governmentDistrict
    }

    fun toDto() = OrtDto(id.value.toString(), postcode, designation, constituency, governmentDistrict)
}

@Serializable
data class OrtDto(
    val city_id: String? = null,
    val postcode: Int,
    val designation: String,
    val constituency: String,
    val governmentDistrict: String
)