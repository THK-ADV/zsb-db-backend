package database

import dto.AdresseDto
import dto.OrtDto
import dto.SchuleDto
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.regex.Pattern

class Ort(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Ort>(Orte) {
        /**
         * persist in db
         */
        fun save(dto: OrtDto) = transaction {
            Ort.new(dto.ort_id) {
                plz = dto.plz
                bezeichnung = dto.bezeichnung
            }
        }
    }

    fun toDto() = OrtDto(id.value, plz, bezeichnung)

    var plz by Orte.plz
    var bezeichnung by Orte.bezeichnung
}

class Adresse(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Adresse>(Adressen) {
        /**
         * persist in db
         */
        fun save(dto: AdresseDto) = transaction {
            Adresse.new(dto.adress_id) {
                strasse = dto.strasse
                hausnummer = dto.hausnummer
                ort = Ort.save(dto.ort)
            }
        }
    }

    fun toDto() = AdresseDto(id.value, strasse, hausnummer, ort.toDto())

    var strasse by Adressen.strasse
    var hausnummer by Adressen.hausnummer
    var ort by Ort referencedOn Adressen.ort
}

class Schule(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Schule>(Schulen) {
        /**
         * persist in db
         */
        fun save(dto: SchuleDto) = transaction {
            if (validateMail(dto.stubo_mail) || validateMail(dto.schulleitung_mail)) {
                Schule.new(dto.schule_id) {
                    schulform = dto.schulform
                    schwerpunkt = dto.schwerpunkt
                    kooperationsvertrag = dto.kooperationsvertrag
                    adresse = Adresse.save(dto.adresse)
                    schulleitung_mail = dto.schulleitung_mail
                    stubo_mail = dto.stubo_mail
                    schueleranzahl = dto.schueleranzahl
                    kaoa_hochschule = dto.kaoa_hochschule
                    talentscouting = dto.talentscouting
                }
            } else null
        }
    }

    fun toDto() = SchuleDto(
        id.value,
        schulform,
        schwerpunkt,
        kooperationsvertrag,
        adresse.toDto(),
        schulleitung_mail,
        stubo_mail,
        schueleranzahl,
        kaoa_hochschule,
        talentscouting
    )

    var schulform by Schulen.schulform
    var schwerpunkt by Schulen.schwerpunkt
    var kooperationsvertrag by Schulen.kooperationsvertrag
    var adresse by Adresse referencedOn Schulen.adress_id
    var schulleitung_mail by Schulen.schulleitung_mail
    var stubo_mail by Schulen.stubo_mail
    var schueleranzahl by Schulen.schueleranzahl
    var kaoa_hochschule by Schulen.kaoa_hochschule
    var talentscouting by Schulen.talentscouting
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
