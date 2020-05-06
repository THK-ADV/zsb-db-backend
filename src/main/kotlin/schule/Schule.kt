package schule

import adresse.Adresse
import error_handling.MailNotValidException
import error_handling.SchulformNotValidException
import error_handling.ZsbException
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import schule.table.Schulen
import utilty.validateMail

class Schule(id: EntityID<Int>) : IntEntity(id) {
    var schulform by Schulen.schulform
    var schwerpunkt by Schulen.schwerpunkt
    var kooperationsvertrag by Schulen.kooperationsvertrag
    var adresse by Adresse referencedOn Schulen.adress_id
    var schulleitung_mail by Schulen.schulleitung_mail
    var stubo_mail by Schulen.stubo_mail
    var schueleranzahl by Schulen.schueleranzahl
    var kaoa_hochschule by Schulen.kaoa_hochschule
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


                val schule: Schule = if (dto.schule_id == null) new {
                    update(dto, adresse)
                } else {
                    val old = Schule[dto.schule_id]
                    old.update(dto, adresse)

                    Schule[dto.schule_id]
                }

                Result.success(schule)
            }
        }

        private fun validateDto(dto: SchuleDto): ZsbException? {
            if (!validateMail(dto.stubo_mail))
                return MailNotValidException("stubo mail is not a valid email.")
            if (!validateMail(dto.schulleitung_mail))
                return MailNotValidException("schulleitungs mail is not a valid email.")
            if (Schulform.getDescById(dto.schulform) == null)
                return SchulformNotValidException("This is not a valid index for Schulform.")

            return null
        }
    }

    private fun update(dto: SchuleDto, adresse: Adresse) {
        this.schulform = dto.schulform
        this.schwerpunkt = dto.schwerpunkt
        this.kooperationsvertrag = dto.kooperationsvertrag
        this.adresse = adresse
        this.schulleitung_mail = dto.schulleitung_mail
        this.stubo_mail = dto.stubo_mail
        this.schueleranzahl = dto.schueleranzahl
        this.kaoa_hochschule = dto.kaoa_hochschule
        this.talentscouting = dto.talentscouting
    }

    fun toDto() = SchuleDto(
        id.value,
        schulform,
        schwerpunkt,
        kooperationsvertrag,
        adresse.id.value,
        schulleitung_mail,
        stubo_mail,
        schueleranzahl,
        kaoa_hochschule,
        talentscouting
    )
}