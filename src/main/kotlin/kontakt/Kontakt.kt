package kontakt

import error_handling.MailNotValidException
import error_handling.ZsbException
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.validateMail
import java.util.*

object Kontakte : UUIDTable() {
    val name = text("name")
    val email = text("email")
    val funktion = integer("funktion")
}

class Kontakt(id: EntityID<UUID>) : UUIDEntity(id) {
    var name by Kontakte.name
    var email by Kontakte.email
    var funktion by Kontakte.funktion

    companion object : UUIDEntityClass<Kontakt>(Kontakte) {
        fun save(dto: KontaktDto): Result<Kontakt> = transaction {
            // validate email
            val exception = validateDto(dto)
            if (exception != null) return@transaction Result.failure(exception)

            // update given ID
            if (dto.uuid != null) {
                val uuid = UUID.fromString(dto.uuid)
                val old = Kontakt[uuid]
                old.update(dto)
                return@transaction Result.Companion.success(Kontakt[uuid])
            }

            // or search for match / create new
            val kontakt: Kontakt = findMatchedKontakt(dto) ?: new { update(dto) }
            Result.success(kontakt)
        }

        // search for existing kontakt
        private fun findMatchedKontakt(dto: KontaktDto): Kontakt? {
            val matchedKontakte = Kontakt.find {
                (Kontakte.name eq dto.name)
                    .and(Kontakte.email eq dto.email)
            }
            return if (matchedKontakte.empty()) null else matchedKontakte.first()
        }

        private fun validateDto(dto: KontaktDto): ZsbException? {
            // validation should now happen in Kontakt
            if (!validateMail(dto.email))
                return MailNotValidException("stubo mail is not a valid email.")

            return null
        }
    }

    private fun update(dto: KontaktDto) {
        this.name = dto.name
        this.email = dto.email
        this.funktion = dto.funktion ?: KontaktFunktion.OTHER.ordinal
    }

    fun toDto() = KontaktDto(id.value.toString(), name, email, funktion)
}