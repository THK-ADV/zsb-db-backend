package database

import api.MailNotValidException
import dto.AdresseDto
import dto.OrtDto
import dto.SchuleDto
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.regex.Pattern

class Ort(id: EntityID<Int>) : IntEntity(id) {
    var plz by Orte.plz
    var bezeichnung by Orte.bezeichnung

    companion object : IntEntityClass<Ort>(Orte) {
        /**
         * persist in db
         */
        fun save(dto: OrtDto): Result<Ort> {
            val ort = transaction {
                Ort.new(dto.ort_id) {
                    this.plz = dto.plz
                    this.bezeichnung = dto.bezeichnung
                }
            }

            return Result.success(ort)
        }

    }

    fun toDto() = OrtDto(id.value, plz, bezeichnung)
}

class Adresse(id: EntityID<Int>) : IntEntity(id) {
    var strasse by Adressen.strasse
    var hausnummer by Adressen.hausnummer
    var ort by Ort referencedOn Adressen.ort

    companion object : IntEntityClass<Adresse>(Adressen) {
        /**
         * persist in db
         */
        fun save(dto: AdresseDto): Result<Adresse> = transaction {
            val ort = Ort[dto.ort_id] // TODO catch exceptions?

            val adresse = Adresse.new(dto.adress_id) {
                this.strasse = dto.strasse
                this.hausnummer = dto.hausnummer
                this.ort = ort
            }

            Result.success(adresse)
        }
    }

    fun toDto() = AdresseDto(id.value, strasse, hausnummer, ort.id.value)
}

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

/**
 * https://gist.github.com/ironic-name/f8e8479c76e80d470cacd91001e7b45b
 */
fun validateMail(mail: String): Boolean = Pattern.compile(
    "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
).matcher(mail).matches()
