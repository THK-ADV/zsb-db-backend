package database

import adresse.Adresse
import adresse.Adressen
import kontakt.Kontakt
import kontakt.Kontakte
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import ort.Ort
import ort.Orte
import schule.SchulKontakte
import schule.Schule
import schule.Schulen
import utilty.ColoredLogging
import java.util.*

fun recreateTablesAndFillWithDummyData() {
    val log = ColoredLogging(KotlinLogging.logger {})

    // recreate DB
    transaction {
        // addLogger(StdOutSqlLogger)
        SchemaUtils.drop(SchulKontakte)
        SchemaUtils.drop(Schulen)
        SchemaUtils.drop(Adressen)
        SchemaUtils.drop(Orte)
        SchemaUtils.drop(Kontakte)

        SchemaUtils.create(Orte, Adressen, Schulen, Kontakte, SchulKontakte)
    }

    // create Orte
    val orte = transaction {
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

        listOf(gm, kuerten, cologne)
    }

    // create adressen
    val adressen = transaction {
        val elementary = Adresse.new {
            strasse = "Am Glockenberg"
            hausnummer = "10"
            ort = orte[1]
        }

        val middle = Adresse.new {
            strasse = "Robert-Koch-Straße"
            hausnummer = "75"
            ort = orte[0]
        }

        val high = Adresse.new {
            strasse = "An den Ringen"
            hausnummer = "51a"
            ort = orte[2]
        }

        listOf(elementary, middle, high)
    }

    // create kontakte
    val kontaktList = transaction {
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

        listOf(contactA, contactB, contactC)
    }

    // create schulen
    transaction {
        Schule.new(UUID.randomUUID()) {
            schulform = 1
            schulname = "Tigerentenclub"
            schwerpunkt = "Kinder"
            kooperationsvertrag = false
            adresse = adressen.first()
            anzahlSus = 2
            kaoaHochschule = false
            talentscouting = true
            kontakte = SizedCollection(listOf(kontaktList[1]))
        }
    }

    transaction {
        Schule.new(UUID.randomUUID()) {
            schulform = 2
            schulname = "Hermann-Voss-Realschule"
            schwerpunkt = "Jugendliche"
            kooperationsvertrag = true
            adresse = adressen[1]
            anzahlSus = 5
            kaoaHochschule = true
            talentscouting = false
            kontakte = SizedCollection(listOf(kontaktList[0], kontaktList[2]))
        }
    }

    transaction {
        Schule.new(UUID.randomUUID()) {
            schulform = 3
            schulname = "Uni Köln"
            schwerpunkt = "Studenten"
            kooperationsvertrag = true
            adresse = adressen.last()
            anzahlSus = 7
            kaoaHochschule = true
            talentscouting = true
            kontakte = SizedCollection(listOf(kontaktList[1]))
        }
    }

    log.info("Loaded dummy data")
}
