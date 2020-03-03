package database

import mu.KotlinLogging
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun recreateTablesAndFillWithDummyData() {
    val logger = KotlinLogging.logger {}

    // init db
    DbSettings.db

    transaction {
//        addLogger(StdOutSqlLogger)

        SchemaUtils.drop(Schulen)
        SchemaUtils.drop(Adressen)
        SchemaUtils.drop(Orte)

        SchemaUtils.create(Orte, Adressen, Schulen)

        val gm = Ort.new {
            plz = 51643
            bezeichnung = "Gummersbach"
        }

        val kuerten = Ort.new {
            plz = 51515
            bezeichnung = "Kürten"
        }

        val cologne = Ort.new {
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
            schulform = "Grundschule"
            schwerpunkt = "Kinder"
            kooperationsvertrag = false
            adresse = elementary
            schulleitung_mail = "masterHand@elementaryGB.de"
            stubo_mail = "stubo@elemenatryGB.de"
            schueleranzahl = 250
            kaoa_hochschule = false
            talentscouting = true
        }

        Schule.new {
            schulform = "Realschule"
            schwerpunkt = "Jugentliche"
            kooperationsvertrag = true
            adresse = middle
            schulleitung_mail = "mediumPeter@middleGM.de"
            stubo_mail = "stubo@mediumGM.de"
            schueleranzahl = 500
            kaoa_hochschule = true
            talentscouting = false
        }

        Schule.new {
            schulform = "Hochschule"
            schwerpunkt = "Studenten"
            kooperationsvertrag = true
            adresse = high
            schulleitung_mail = "rank@highCL.de"
            stubo_mail = "rank@highCL.de"
            schueleranzahl = 750
            kaoa_hochschule = true
            talentscouting = true
        }

        logger.info { "Loaded dummy data" }
    }
}