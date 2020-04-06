package schule

import adresse.Adresse
import error_handling.MailNotValidException
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import schule.table.Schulen
import utilty.validateMail

class Schule(id: EntityID<Int>) : IntEntity(id) {
    var schulname by Schulen.schulname
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
            if (!validateMail(dto.stubo_mail) || !validateMail(dto.schulleitung_mail))
                return Result.failure(MailNotValidException("stubo or schulleitungs mail is not a valid email"))

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
    }

    private fun update(dto: SchuleDto, adresse: Adresse) {
        this.schulname = dto.name
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
        schulname,
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