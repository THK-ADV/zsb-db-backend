package legacy_import

import adresse.Adresse
import adresse.AdresseDto
import kontakt.Kontakt
import kontakt.KontaktDto
import kontakt.KontaktFunktion
import ort.Ort
import ort.OrtDto
import schule.AnzahlSus
import schule.Schule
import schule.SchuleDto
import schule.Schulform
import java.io.File

class CsvImport(file: File) {
    private val lines = mutableListOf<List<String>>()


    init {
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
                bezeichnung = plzOrt.last(),
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
                ort_id = ort.id.value
            )

            val adresse = Adresse.save(adresseDto).getOrNull()
            if (adresse == null) {
                ImportLog.error("Could not read ADRESSE in line: $line")
                return@forEach
            }


            // TODO change db model to match n:m for kontakte:schule
            val kontaktADto = parseKontakt(
                line[SchuleIndices.mailKontaktPerson],
                line[SchuleIndices.nameStuBo],
                KontaktFunktion.STUBO.id
            ).first()
            val kontaktA = Kontakt.save(kontaktADto).getOrNull()

            val kontaktBDto =
                parseKontakt(line[SchuleIndices.mailKontaktPerson2], line[SchuleIndices.kontaktPerson2]).first()
            val kontaktB = Kontakt.save(kontaktBDto).getOrNull()

            val stuboDto = parseKontakt(
                line[SchuleIndices.mailStuBO],
                line[SchuleIndices.nameStuBO2],
                KontaktFunktion.STUBO.id
            ).first()
            val stubo = Kontakt.save(stuboDto).getOrNull()

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
                adresse.id.value,
                kontaktA?.id?.value?.toString(),
                kontaktB?.id?.value?.toString(),
                stubo?.id?.value?.toString(),
                kAoa,
                talent
            )

            Schule.save(schuleDto).getOrNull() ?: ImportLog.error("Could not read SCHULE in line: $line")
        }
    }

    /**
     * If [text] is equal to "ja" return true, else false
     */
    private fun parseToBoolean(text: String): Boolean = text.toLowerCase().trim() == "ja"

    private fun parseKontakt(names: String, emails: String, function: Int? = null): List<KontaktDto> {
        val kontakte = mutableListOf<KontaktDto>()

        // TODO fix empty kontakt in Regenbogenschule

        // split multiple kontakte by "und"
        val splitNames = names.split("und")
        val splitEmails = emails.split("und")

        // count of available kontakt data
        val count = if (splitEmails.size > splitNames.size) splitEmails.size else splitNames.size
        repeat(count) { i ->
            val name = splitNames.getOrNull(i)
            val email = splitEmails.getOrNull(i)

            val nameAndFunktion = name?.let { parseKontaktFunktion(it) }

            val kontaktDto = KontaktDto(
                null,
                nameAndFunktion?.first ?: "",
                email ?: "",
                function ?: nameAndFunktion?.second?.id ?: KontaktFunktion.UNKNOWN.id
            )
            kontakte.add(kontaktDto)
        }

        return kontakte
    }

    /**
     * parse a [KontaktFunktion] from given [text], by extracting the first matching description;
     * matching descriptions must be in brackets ()
     * @return a [Pair] of the [text] without the cut function description and the found [KontaktFunktion]
     *
     * Example [text]: Frau Schulz, Kate (Schulleitung)
     * Example return: Pair("Frau Schulz, Kate", KontaktFunktion.SCHULLEITUNG)
     */
    private fun parseKontaktFunktion(text: String): Pair<String, KontaktFunktion> {
        if (!text.contains('(') || !text.contains(')')) return Pair(text, KontaktFunktion.UNKNOWN)

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
