package legacy_import

import model.adresse.Adresse
import model.adresse.AdresseDto
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

data class KontaktLight(val name: String, val vorname: String, val anrede: Anrede, val funktion: KontaktFunktion?)

class CsvImport(file: File) {
    private val lines = mutableListOf<List<String>>()


    init {
        ImportLog.clear()
        val rawLines = file.readLines()
        rawLines.forEach { rawLine ->
            lines.add(rawLine.split(";"))
        }
    }

    fun parseSchule() {
        // Schulname ; Name StuBO ; Mail Kontaktperson ; Schulform ; Kooperationsvertrag ; Nummer, Straße ; PLZ, Ort ;
        // Regierungsbezirk ; Kreis ; Kontaktperson ; Mail Kontaktperson ;
        // Name StuBO ; Mail StuBO ; Anzahl SuS ; KAoA ; Talentscout

        lines.forEach { line ->
            // skip first line
            if (line.last() == "Talentscout") return@forEach

            val plzOrt = line[SchuleIndices.plz_ort].split(",")
            val ortDto = OrtDto(
                plz = plzOrt.first().trim().toInt(),
                bezeichnung = plzOrt.last().trim(),
                kreis = line[SchuleIndices.kreis],
                regierungsbezirk = line[SchuleIndices.regierungsbezirk]
            )


            val ort = Ort.save(ortDto).getOrNull()
            if (ort == null) {
                ImportLog.error("Could not read ORT in line: $line")
                return@forEach
            }

            val nummerStrasse = line[SchuleIndices.nummer_strasse].split(',', limit = 2)
            val adresseDto = AdresseDto(
                strasse = nummerStrasse.last(),
                hausnummer = nummerStrasse.first(),
                ort_id = ort.id.value.toString()
            )

            val adresse = Adresse.save(adresseDto).getOrNull()
            if (adresse == null) {
                ImportLog.error("Could not read ADRESSE in line: $line")
                return@forEach
            }


            val kontakteIds = mutableListOf<String>()
            val kontakteADto = parseKontakte(
                line[SchuleIndices.nameStuBo],
                line[SchuleIndices.mailKontaktPerson],
                KontaktFunktion.STUBO.id
            )
            kontakteADto.forEach { kontaktDto ->
                Kontakt.save(kontaktDto).getOrNull()?.let {
                    kontakteIds.add(it.id.value.toString())
                }
            }

            val kontakteBDto = parseKontakte(
                line[SchuleIndices.kontaktPerson2],
                line[SchuleIndices.mailKontaktPerson2]
            )

            kontakteBDto.forEach { kontaktDto ->
                Kontakt.save(kontaktDto).getOrNull()?.let {
                    kontakteIds.add(it.id.value.toString())
                }
            }

            val stuboDtos = parseKontakte(
                line[SchuleIndices.nameStuBO2],
                line[SchuleIndices.mailStuBO],
                KontaktFunktion.STUBO.id
            )
            stuboDtos.forEach { kontaktDto ->
                Kontakt.save(kontaktDto).getOrNull()?.let {
                    kontakteIds.add(it.id.value.toString())
                }
            }

            val anzahlSus = AnzahlSus.getObjectByString(line[SchuleIndices.anzahlSuS])
            val schulform = Schulform.getSchulformByDesc(line[SchuleIndices.Schulform])
            val schulname = line[SchuleIndices.schulname]
            val kooperationsvertrag = parseToBoolean(line[SchuleIndices.Kooperationsvertrag])
            val kAoa = parseToBoolean(line[SchuleIndices.kAoA])
            val talent = parseToBoolean(line[SchuleIndices.talentscout])


            val schuleDto = SchuleDto(
                null,
                schulname,
                schulform.id,
                null,
                anzahlSus.id,
                kooperationsvertrag,
                adresse.id.value.toString(),
                kontakteIds,
                kAoa,
                if (kAoa) 0 else -1,
                talent,
                if (talent) 0 else -1
            )

            Schule.save(schuleDto).getOrNull() ?: ImportLog.error("Could not read SCHULE in line: $line")
        }
    }

    /**
     * If [text] is equal to "ja" return true, else false
     */
    private fun parseToBoolean(text: String): Boolean = text.toLowerCase().trim() == "ja"

    private fun parseKontakte(names: String, emails: String, function: Int? = null): List<KontaktDto> {
        val kontakte = mutableListOf<KontaktDto>()

        // split multiple kontakte by "und"
        val splitNames = names.split("und")
        val splitEmails = emails.split("und")

        // count of available model.kontakt data
        val count = if (splitEmails.size > splitNames.size) splitEmails.size else splitNames.size
        repeat(count) { i ->
            val kontaktLightString = splitNames.getOrNull(i)
            val email = splitEmails.getOrNull(i)

            val kontaktLight = kontaktLightString?.let { parseKontaktLight(it) }

            val kontaktDto = KontaktDto(
                null,
                kontaktLight?.name ?: "",
                kontaktLight?.vorname ?: "",
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
     * parse a [KontaktLight] (Name, Vorname, [Anrede] and [KontaktFunktion]) from given [text],
     * by extracting the first matching description; matching descriptions must be in brackets ()
     *
     * @param [text] Name of a Person with Anrede and Funktion e.g. "Frau Schulz, Kate (Schulleitung)"
     *
     * @return a [Pair] of the [text] without the cut function description and the found [KontaktFunktion]
     */
    private fun parseKontaktLight(text: String): KontaktLight {
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

        return KontaktLight(name, vorname, anrede, funktion)
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
