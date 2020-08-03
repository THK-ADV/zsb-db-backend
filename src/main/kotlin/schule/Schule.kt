package schule

import adresse.Adresse
import adresse.Adressen
import error_handling.SchulformNotValidException
import error_handling.ZsbException
import kontakt.Kontakt
import kontakt.Kontakte
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object Schulen : IntIdTable() {
    val schulname = text("schulname")
    val schulform = integer("schulform")
    val schwerpunkt = text("schwerpunkt")
    val anzahlSus = integer("anzahl_sus")
    val kooperationsvertrag = bool("kooperationsvertrag")
    val adress_id = reference("adress_id", Adressen)
    val kontakt_a = reference("kontakt_a", Kontakte).nullable()
    val kontakt_b = reference("kontakt_b", Kontakte).nullable()
    val stubo_kontakt = reference("stubo_kontakt", Kontakte).nullable()
    val kaoa_hochschule = bool("kaoa_hochschule")
    val talentscouting = bool("talentscouting")
}

// TODO ID in uuids umstellen
class Schule(id: EntityID<Int>) : IntEntity(id) {
    var schulname by Schulen.schulname
    var schulform by Schulen.schulform
    var schwerpunkt by Schulen.schwerpunkt
    var anzahlSus by Schulen.anzahlSus
    var kooperationsvertrag by Schulen.kooperationsvertrag
    var adresse by Adresse referencedOn Schulen.adress_id
    var kontaktA by Kontakt optionalReferencedOn Schulen.kontakt_a
    var kontaktB by Kontakt optionalReferencedOn Schulen.kontakt_b
    var stuboKontakt by Kontakt optionalReferencedOn Schulen.stubo_kontakt
    var kaoaHochschule by Schulen.kaoa_hochschule
    var talentscouting by Schulen.talentscouting

    companion object : IntEntityClass<Schule>(Schulen) {
        /**
         * persist in db
         */
        fun save(dto: SchuleDto): Result<Schule> {
            val exception = validateDto(dto)
            if (exception != null) return Result.failure(exception)

            return transaction {
                val adresse = Adresse[dto.adress_id]
                val kontaktA = dto.kontakt_a_id?.let { Kontakt[UUID.fromString(it)] }
                val kontaktB = dto.kontakt_b_id?.let { Kontakt[UUID.fromString(it)] }
                val stuboKontakt = dto.stubo_kontakt_id?.let { Kontakt[UUID.fromString(it)] }

                val schule: Schule = if (dto.schule_id == null) new {
                    update(dto, adresse, kontaktA, kontaktB, stuboKontakt)
                } else {
                    val old = Schule[dto.schule_id]
                    old.update(dto, adresse, kontaktA, kontaktB, stuboKontakt)

                    Schule[dto.schule_id]
                }

                Result.success(schule)
            }
        }

        private fun validateDto(dto: SchuleDto): ZsbException? {
            // validation should now happen in Kontakt
//            if (!validateMail(dto.stubo_kontakt))
//                return MailNotValidException("stubo mail is not a valid email.")
//            if (!validateMail(dto.kontakt_b))
//                return MailNotValidException("schulleitungs mail is not a valid email.")
            if (Schulform.getDescById(dto.schulform) == null)
                return SchulformNotValidException("This is not a valid index for Schulform.")

            return null
        }
    }

    private fun update(dto: SchuleDto, adresse: Adresse, kontaktA: Kontakt?, kontaktB: Kontakt?, stuboKontakt: Kontakt?) {
        this.schulname = dto.name
        this.schulform = dto.schulform
        this.schwerpunkt = dto.schwerpunkt.toString() // TODO find better solution. Null values in DB?
        this.anzahlSus = dto.anzahl_sus
        this.kooperationsvertrag = dto.kooperationsvertrag
        this.adresse = adresse
        this.kontaktA = kontaktA
        this.kontaktB = kontaktB
        this.stuboKontakt = stuboKontakt
        this.kaoaHochschule = dto.kaoa_hochschule
        this.talentscouting = dto.talentscouting
    }

    fun toDto() = SchuleDto(
        id.value,
        schulname,
        schulform,
        schwerpunkt,
        anzahlSus,
        kooperationsvertrag,
        adresse.id.value,
        kontaktA?.id?.value?.toString(),
        kontaktB?.id?.value?.toString(),
        stuboKontakt?.id?.value?.toString(),
        kaoaHochschule,
        talentscouting
    )
}