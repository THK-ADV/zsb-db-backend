package legacy_import

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import model.address.Adresse
import model.address.AdresseDto
import model.kontakt.Kontakt
import model.kontakt.KontaktDto
import model.kontakt.enum.Anrede
import model.kontakt.enum.KontaktFunktion
import model.ort.Ort
import model.ort.OrtDto
import model.schule.Schule
import model.schule.SchuleDto
import model.schule.enum.AnzahlSus
import model.schule.enum.Schulform
import java.io.File

data class ContactLight(val name: String, val vorname: String, val anrede: Anrede, val funktion: KontaktFunktion?)

object CsvImport {

    init {
        ImportLog.clear()
    }

    fun parseSchule(file: File) {
        // Schulname;Schulform;Schwerpunkt;Kooperationsvertrag;Nummer, Straße;PLZ;Ort;Regierungsbezirk;Kreis;
        // Kontaktperson;Mail Kontaktperson;Name StuBO;Mail StuBO;Anzahl SuS;KAoA;Talentscout
        val rows = csvReader{
            charset = "windows-1252"
            delimiter = ';'
        }.readAllWithHeader(file)

        rows.forEach { row ->
            val ort = parseOrt(row["PLZ, Ort"], row["Regierungsbezirk"], row["Kreis"])
                .also { logError(row.toList(), it, "ort") }
                ?: return@forEach
            val adresse = parseAdresse(row["Nummer, Straße"], ort)
                .also{ logError(row.toList(), it, "adresse") }
                ?: return@forEach
            val kontaktpersonen = parseKontakte(row["Kontaktperson"], row["Mail Kontaktperson"])
            val stuboKontakte = parseKontakte(row["Name StuBO"], row["Mail StuBO"], KontaktFunktion.STUBO.id)
            val kontakte = saveKontakte(kontaktpersonen, stuboKontakte)
            val kontaktIds = kontakte.map { it.id.value.toString() }
            val schwerpunkt = row["Schwerpunkt"]
                .also{ logError(row.toList(), it, "Schwerpunkt") }
                ?: return@forEach
            val schulname = row["Schulname"]
                .also{ logError(row.toList(), it, "Schulname") }
                ?: return@forEach
            val anzahlSus = AnzahlSus.getObjectByString(row["Anzahl SuS"] ?: "")
            val schulform = Schulform.getSchulformByDesc(row["Schulform"] ?: "")
            val kooperationsvertrag = parseToBoolean(row["Kooperationsvertrag"] ?: "")
            val kAoa = parseToBoolean(row["KAoA"] ?: "")
            val talent = parseToBoolean(row["Talentscout"] ?: "")

            val schuleDto = SchuleDto(
                null,
                schulname,
                schulform.id,
                schwerpunkt,
                anzahlSus.id,
                kooperationsvertrag,
                adresse.id.value.toString(),
                kontaktIds,
                kAoa,
                if (kAoa) 0 else -1,
                talent,
                if (talent) 0 else -1
            )

            Schule.save(schuleDto).getOrNull()
                .also { logError(row, it, "LINE") }
        }
    }

    private fun saveKontakte(kontaktpersonen: List<KontaktDto>, stuboKontakte: List<KontaktDto>): List<Kontakt> {
        val kontakte = mutableListOf<Kontakt>()
        (kontaktpersonen + stuboKontakte).forEach { kontaktDto ->
            Kontakt.save(kontaktDto).getOrNull()?.let { kontakt ->
                kontakte.add(kontakt)
            }
        }
        return kontakte
    }

    private fun parseAdresse(adresseString: String?, ort: Ort): Adresse? {
        val nummerStrasse = adresseString?.split(',', limit = 2) ?: return null
        if (nummerStrasse.size < 2) return null
        val adresseDto = AdresseDto(
            strasse = nummerStrasse.last(),
            hausnummer = nummerStrasse.first(),
            ort_id = ort.id.value.toString()
        )

        return Adresse.save(adresseDto).getOrNull()
    }

    private fun parseOrt(plzOrtString: String?, kreis: String?, regierungsbezirk: String?): Ort? {
        val plzOrt = plzOrtString?.split(",") ?: return null
        if (plzOrt.size < 2) return null
        val ortDto = OrtDto(
            plz = plzOrt.first().trim().toInt(),
            bezeichnung = plzOrt.last().trim(),
            kreis = kreis?.trim() ?: "",
            regierungsbezirk = regierungsbezirk?.trim() ?: ""
        )
        return Ort.save(ortDto).getOrNull()
    }

    private fun logError(line: Any, nullable: Any?, hint: String) {
        if (nullable == null) ImportLog.error("Could not read '$hint' in line: $line")
    }

    /**
     * If [text] is equal to "ja" return true, else false
     */
    private fun parseToBoolean(text: String): Boolean = text.toLowerCase().trim() == "ja"

    private fun parseKontakte(names: String?, emails: String?, function: Int? = null): List<KontaktDto> {
        val kontakte = mutableListOf<KontaktDto>()

        // split multiple kontakte by "und"
        val splitNames = names?.split("und") ?: listOf()
        val splitEmails = emails?.split("und") ?: listOf()

        // count of available model.kontakt data
        val count = if (splitEmails.size > splitNames.size) splitEmails.size else splitNames.size
        repeat(count) { i ->
            val kontaktLightString = splitNames.getOrNull(i)
            val email = splitEmails.getOrNull(i)

            val kontaktLight = kontaktLightString?.let { parseKontaktLight(it) }

            val kontaktDto = KontaktDto(
                null,
                kontaktLight?.name?.trim() ?: "",
                kontaktLight?.vorname?.trim() ?: "",
                kontaktLight?.anrede?.id ?: Anrede.UNKNOWN.id,
                email?.trim() ?: "",
                kontaktLight?.funktion?.id ?: function ?: KontaktFunktion.UNKNOWN.id
            )
            if (kontaktDto.isValid())
                kontakte.add(kontaktDto)
        }

        return kontakte
    }

    /**
     * parse a [ContactLight] (Name, Vorname, [Anrede] and [KontaktFunktion]) from given [text],
     * by extracting the first matching description; matching descriptions must be in brackets ()
     *
     * @param [text] Name of a Person with Anrede and Funktion e.g. "Frau Schulz, Kate (Schulleitung)"
     *
     * @return a [Pair] of the [text] without the cut function description and the found [KontaktFunktion]
     */
    private fun parseKontaktLight(text: String): ContactLight {
        // Example fullName: Frau Schulz, Kate
        // (vorname can be missing)
        var (fullName, funktion) = parseKontaktFunktion(text)

        // parse vorname
        val vorname = if (fullName.contains(",")) {
            val nameSplit = fullName.split(",")
            fullName = nameSplit.first().trim()
            nameSplit.last().trim() // vorname
        } else ""

        // remaining full name example: Frau Schulz
        val anredeNameSplit = fullName.trim().split(" ")
        val name = anredeNameSplit.last().trim()
        val anrede = Anrede.getObjectByString(anredeNameSplit.first().trim())

        return ContactLight(name, vorname, anrede, funktion)
    }

    private fun parseKontaktFunktion(text: String): Pair<String, KontaktFunktion?> {
        if (!text.contains('(') || !text.contains(')')) return Pair(text, null)

        // Example split: Schulleitung)
        val split = text.split('(', limit = 2)

        // Example remainingText: Frau Schulz, Kate
        val remainingText = split.first()

        // Example functionDesc: Schulleitung
        val functionDesc = split.last().trim(')', ' ')

        val funktion = KontaktFunktion.getFunktionByDesc(functionDesc)

        return Pair(remainingText, funktion)
    }
}
