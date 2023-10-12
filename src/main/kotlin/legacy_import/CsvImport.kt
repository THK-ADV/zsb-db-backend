package legacy_import

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import model.address.Adresse
import model.address.AdresseDto
import model.kontakt.Kontakt
import model.kontakt.KontaktDto
import model.kontakt.enum.Anrede
import model.kontakt.enum.KAoABetreuung
import model.kontakt.enum.KontaktFunktion
import model.kontakt.enum.Talentscout
import model.ort.Ort
import model.ort.OrtDto
import model.schule.Schule
import model.schule.SchuleDto
import model.schule.enum.Kooperationspartner
import model.schule.enum.Schulform
import java.io.File

private fun String?.ensureNonEmpty(): String =
    if (this == null)
        throw Exception("String is empty")
    else if (this.isBlank() || this.isEmpty())
        throw Exception("String is empty")
    else this

object CsvImport {

    fun toSchooltype(s: String?): Schulform =
        s?.let { Schulform.fromDesc(it.trim()) } ?: Schulform.KEINE

    fun toCooperationpartner(s: String?): Kooperationspartner =
        s?.let { Kooperationspartner.fromDesc(it.trim()) } ?: Kooperationspartner.KEINE

    fun toSalutation(s: String?): Anrede =
        s?.let { Anrede.fromDesc(it.trim()) } ?: Anrede.KEINE

    // TODO the data-import file contains abbreviations like 'StuBO' or 'ASP BA', but the actual
    //  descriptions are spelled out, e.g. 'Studien- und Berufswahlkoordinator*in' or 'Ansprechpartner*innen...'.
    //  thus, those contact features do not match and fall back to OTHER(25)
    fun toContactFeature(s: String?): KontaktFunktion =
        s?.let { KontaktFunktion.fromDesc(it.trim()) } ?: KontaktFunktion.KEINE

    fun toAddress(street: String?, houseNumber: String?, city: Ort): Adresse {
        val adresseDto = AdresseDto(
            street = street.ensureNonEmpty().trim(),
            houseNumber = houseNumber?.trim() ?: "1",
            city_id = city.id.value.toString()
        )
        return Adresse.save(adresseDto).getOrThrow()
    }

    fun toCity(postcode: String?, designation: String?, constituency: String?, governmentDistrict: String?): Ort {
        val ortDto = OrtDto(
            postcode = postcode.ensureNonEmpty().trim().toInt(),
            designation = designation.ensureNonEmpty().trim(),
            constituency = constituency.ensureNonEmpty().trim(),
            governmentDistrict = governmentDistrict.ensureNonEmpty().trim()
        )
        return Ort.save(ortDto).getOrThrow()
    }

    fun toContact(names: String?, salutations: String?, features: String?, emails: String?): List<Kontakt> {
        if (names == null || salutations == null || features == null || emails == null)
            return emptyList()

        val nameList = names.split(";")
        val salutationList = salutations.split(";")
        val featureList = features.split(";")
        val emailList = emails.split(";")

        if (nameList.size != salutationList.size || nameList.size != featureList.size || nameList.size != emailList.size)
            return emptyList()

        fun createContact(res: Pair<Pair<Pair<String, String>, String>, String>): Kontakt {
            val name = res.first.first.first.split(",")
            val surname = name.getOrNull(0) ?: ""
            val firstname = name.getOrNull(1) ?: ""
            val salutation = res.first.first.second
            val feature = res.first.second
            val email = res.second

            val contactDto = KontaktDto(
                surname = surname.trim(),
                firstname = firstname.trim(),
                salutation = this.toSalutation(salutation).id,
                feature = this.toContactFeature(feature).id,
                email = email.trim()
            )
            return Kontakt.save(contactDto).getOrThrow()
        }

        return nameList.zip(salutationList).zip(featureList).zip(emailList)
            .map(::createContact)
    }

    fun toKAoASupervisor(s: String?): KAoABetreuung =
        s?.let { KAoABetreuung.fromDesc(it.trim()) } ?: KAoABetreuung.KEINE

    fun toTalentscout(s: String?): Talentscout =
        s?.let { Talentscout.fromDesc(it.trim()) } ?: Talentscout.KEINE

    fun parseSchool(file: File) {
        val rows = csvReader {
            delimiter = ';'
        }.readAllWithHeader(file)

        rows.forEach { row ->
            val name = row["Schulname"].ensureNonEmpty().trim()
            val type = toSchooltype(row["Schulform"])
            val city = toCity(row["PLZ"], row["Ort"], row["Regierungsbezirk"], row["Kreis"])
            val address = toAddress(row["Straße"], row["Hausnummer"], city)
            val phonenumber = row["Telefon Schule/Sekretariat"]?.trim() ?: ""
            val email = row["Email Schule/Sekretariat"]?.trim() ?: ""
            val website = row["Schulwebseite"]?.trim() ?: ""
            val comment = row["Kommentar"]?.trim() ?: ""
            val amountStudents11 = row["Schülerzahl EF/11*"]?.trim()?.toIntOrNull() ?: 0
            val amountStudents12 = row["Schülerzahl Q1/12*"]?.trim()?.toIntOrNull() ?: 0
            val amountStudents13 = row["Schülerzahl Q2/13*"]?.trim()?.toIntOrNull() ?: 0
            val contacts = toContact(
                row["Name, Vorname Kontaktpersonen"],
                row["Anrede Kontaktpersonen"],
                row["Funktion Kontaktpersonen"],
                row["Mailadresse Kontaktpersonen"]
            )
            val cooperationpartner = toCooperationpartner(row["Betreuende Hochschule KAoA/Talentscouting"])
            val kaoaSupervisor = toKAoASupervisor(row["Nachname, Vorname KAoA Betreuer*in"])
            val talentscout = toTalentscout(row["Nachname, Vorname Talentscout"])
            val cooperationcontract = parseToBoolean(
                row["Hochschulischen Kooperationsvertrag TH (Allgemeine und spezifische Zusammenarbeit)"]?.trim() ?: "nein"
            )

            val schoolDto = SchuleDto(
                null,
                name,
                type.id,
                comment,
                amountStudents11,
                amountStudents12,
                amountStudents13,
                phonenumber,
                email,
                website,
                cooperationpartner.id,
                kaoaSupervisor.id,
                talentscout.id,
                cooperationcontract,
                address.id.value.toString(),
                contacts_ids = contacts.map { it.id.value.toString() }
            )

            print(schoolDto)
            val school = Schule.save(schoolDto).getOrThrow()
        }
    }

    private fun parseToBoolean(text: String): Boolean = text.lowercase().trim() == "ja"
}
