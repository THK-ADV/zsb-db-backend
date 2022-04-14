package model.kontakt

import error_handling.SalutationNotValidException
import error_handling.MailNotValidException
import error_handling.ZsbException
import kotlinx.serialization.Serializable
import model.kontakt.enum.Anrede
import model.kontakt.enum.KontaktFunktion
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.validateMail
import java.util.*

object Kontakte : UUIDTable() {
    val surname = text("name")
    val firstname = text("vorname")
    val salutation = integer("anrede")
    val email = text("email")
    val feature = integer("funktion")
}

class Kontakt(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var surname by Kontakte.surname
    private var firstname by Kontakte.firstname
    private var salutation by Kontakte.salutation
    private var email by Kontakte.email
    private var feature by Kontakte.feature

    companion object : UUIDEntityClass<Kontakt>(Kontakte) {
        fun save(dto: KontaktDto): Result<Kontakt> = transaction {
            // validate email
            val exception = validateDto(dto)
            if (exception != null) return@transaction Result.failure(exception)

            // update given ID
            if (dto.contact_id != null) {
                val uuid = UUID.fromString(dto.contact_id)
                val old = Kontakt[uuid]
                old.update(dto)
                return@transaction Result.Companion.success(Kontakt[uuid])
            }

            // or search for match / create new
            val kontakt: Kontakt = findMatchedKontakt(dto) ?: new { update(dto) }
            Result.success(kontakt)
        }

        // search for existing model.kontakt
        private fun findMatchedKontakt(dto: KontaktDto): Kontakt? {
            val matchedKontakte = Kontakt.find {
                (Kontakte.surname eq dto.surname)
                    .and(Kontakte.email eq dto.email)
            }
            return if (matchedKontakte.empty()) null else matchedKontakte.first()
        }

        private fun validateDto(dto: KontaktDto): ZsbException? {
            if (!Anrede.values().indices.contains(dto.salutation))
                return SalutationNotValidException("Anrede for ${dto.firstname} is not valid.")

            if (!validateMail(dto.email))
                return MailNotValidException("mail for ${dto.surname} is not a valid email.")

            return null
        }
    }

    private fun update(dto: KontaktDto) {
        this.surname = dto.surname
        this.firstname = dto.firstname
        this.salutation = dto.salutation ?: Anrede.KEINE.ordinal
        this.email = dto.email
        this.feature = dto.feature ?: KontaktFunktion.KEINE.ordinal
    }

    fun toDto() = KontaktDto(id.value.toString(), surname, firstname, salutation, feature, email)
}

@Serializable
data class KontaktDto(
    val contact_id: String? = null,
    val surname: String,
    val firstname: String,
    val salutation: Int? = null,
    val feature: Int? = null,
    val email: String
)
