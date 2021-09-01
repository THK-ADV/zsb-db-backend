package model.adresse

import error_handling.OrtIdNotFoundException
import kotlinx.serialization.Serializable
import model.ort.Ort
import model.ort.OrtDto
import model.ort.Orte
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object Adressen : UUIDTable() {
    val street = varchar("strasse", 250)
    val houseNumber = varchar("hausnummer", 20)
    val city = reference("model/ort", Orte)
}

class Adresse(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var street by Adressen.street
    private var houseNumber by Adressen.houseNumber
    private var city by Ort referencedOn Adressen.city

    companion object : UUIDEntityClass<Adresse>(Adressen) {
        /**
         * persist in db
         */
        fun save(dto: AdresseDto): Result<Adresse> = transaction {
            val ortUUID = UUID.fromString(dto.city_id)

            val ort = anyOrNull { Ort[ortUUID] }
                ?: return@transaction Result.failure<Adresse>(OrtIdNotFoundException("Couldn't update Adresse due to wrong Ort (ID: ${dto.city_id})"))

            val matchedAdressen = Adresse.find {
                (Adressen.city eq ortUUID)
                    .and(Adressen.street eq dto.street)
                    .and(Adressen.houseNumber eq dto.houseNumber)
            }
            val matchedAdresse = if (matchedAdressen.empty()) null else matchedAdressen.first()

            val adresse = when {
                dto.address_id != null -> {
                    val uuid = UUID.fromString(dto.address_id)
                    val old = Adresse[uuid]
                    old.update(dto, ort)
                    Adresse[uuid]
                }
                matchedAdresse != null -> matchedAdresse
                else -> new { update(dto, ort) }
            }

            Result.success(adresse)
        }
    }

    private fun update(dto: AdresseDto, ort: Ort) {
        this.street = dto.street
        this.houseNumber = dto.houseNumber
        this.city = ort
    }

    fun toDto() = AdresseDto(id.value.toString(), street, houseNumber, city.id.value.toString())

    fun toAtomicDto() = AdresseDto(id.value.toString(), street, houseNumber, city.id.value.toString(), city.toDto())
}

@Serializable
data class AdresseDto(
    val address_id: String? = null,
    val street: String,
    val houseNumber: String,
    val city_id: String,
    val city: OrtDto? = null
)