package database

import adresse.Adresse
import adresse.table.Adressen
import kontakt.Kontakt
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ort.Ort
import ort.table.Orte
import schule.Schule
import schule.table.Schulen
import utilty.ColoredLogging

fun recreateTablesAndFillWithDummyData() {
    val log = ColoredLogging(KotlinLogging.logger {})

    transaction {
        // addLogger(StdOutSqlLogger)

        SchemaUtils.drop(Schulen)
        SchemaUtils.drop(Adressen)
        SchemaUtils.drop(Orte)

        SchemaUtils.create(Orte, Adressen, Schulen)

        val gm = Ort.new {
            kreis = "GM"
            regierungsbezirk = "Oberberg."
            plz = 51643
            bezeichnung = "Gummersbach"
        }

        val kuerten = Ort.new {
            kreis = "Kürten"
            regierungsbezirk = "GL"
            plz = 51515
            bezeichnung = "Kürten"
        }

        val cologne = Ort.new {
            kreis = "Südstadt"
            regierungsbezirk = "Köln"
            plz = 50667
            bezeichnung = "Köln"
        }

        val elementary = Adresse.new {
            strasse = "Am Glockenberg"
            hausnummer = "10"
            ort = kuerten
        }

        val middle = Adresse.new {
            strasse = "Robert-Koch-Straße"
            hausnummer = "75"
            ort = gm
        }

        val high = Adresse.new {
            strasse = "An den Ringen"
            hausnummer = "51a"
            ort = cologne
        }

        val contactA = Kontakt.new {
            name = "Alice Meier"
            email = "alice@studio42.org"
            funktion = 1
        }

        val contactB = Kontakt.new {
            name = "Kefan Starsch"
            email = "brutal@hackerz.org"
            funktion = 1
        }

        val contactC = Kontakt.new {
            name = "Hans Peter"
            email = "hans.peter@123.tv"
            funktion = 2
        }

        Schule.new {
            schulform = 1
            schulname = "Tigerentenclub"
            schwerpunkt = "Kinder"
            kooperationsvertrag = false
            adresse = elementary
            stuboKontakt = contactB
            anzahlSus = 2
            kaoaHochschule = false
            talentscouting = true
        }

        Schule.new {
            schulform = 2
            schulname = "Hermann-Voss-Realschule"
            schwerpunkt = "Jugendliche"
            kooperationsvertrag = true
            adresse = middle
            kontaktA = contactA
            stuboKontakt = contactC
            anzahlSus = 5
            kaoaHochschule = true
            talentscouting = false
        }

        Schule.new {
            schulform = 3
            schulname = "Uni Köln"
            schwerpunkt = "Studenten"
            kooperationsvertrag = true
            adresse = high
            kontaktA = contactB
            anzahlSus = 7
            kaoaHochschule = true
            talentscouting = true
        }

        log.info("Loaded dummy data")
    }
}