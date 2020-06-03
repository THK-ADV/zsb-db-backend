package database

import adresse.Adresse
import adresse.table.Adressen
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
            kreis = "Südstad"
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

        Schule.new {
            schulform = 1
            schulname = "Tigerentenclub"
            schwerpunkt = "Kinder"
            kooperationsvertrag = false
            adresse = elementary
            schulleitung_mail = "boss@tigerentenclub.de"
            stubo_mail = "info@tigerentenclub.de"
            schueleranzahl = 250
            kaoa_hochschule = false
            talentscouting = true
        }

        Schule.new {
            schulform = 2
            schulname = "Hermann-Voss-Realschule"
            schwerpunkt = "Jugendliche"
            kooperationsvertrag = true
            adresse = middle
            schulleitung_mail = "boss@hr-real.de"
            stubo_mail = "info@hr-real.de"
            schueleranzahl = 500
            kaoa_hochschule = true
            talentscouting = false
        }

        Schule.new {
            schulform = 3
            schulname = "Uni Köln"
            schwerpunkt = "Studenten"
            kooperationsvertrag = true
            adresse = high
            schulleitung_mail = "boss@uni-koeln.de"
            stubo_mail = "info@uni-koeln.de"
            schueleranzahl = 750
            kaoa_hochschule = true
            talentscouting = true
        }

        log.info("Loaded dummy data")
    }
}