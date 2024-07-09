package database

import model.address.Adressen
import model.kontakt.Kontakte
import model.ort.Orte
import model.schule.SchulKontakte
import model.schule.Schulen
import model.termin.Termine
import model.termin.kontakte.KontakteHochschule
import model.termin.kontakte.KontakteSchule
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun recreateDatabase() {
    transaction {
        // recreate DB
        SchemaUtils.drop(Termine)
        SchemaUtils.drop(KontakteSchule)
        SchemaUtils.drop(KontakteHochschule)
        SchemaUtils.drop(SchulKontakte)
        SchemaUtils.drop(Schulen)
        SchemaUtils.drop(Adressen)
        SchemaUtils.drop(Orte)
        SchemaUtils.drop(Kontakte)

        SchemaUtils.create(
            Orte,
            Adressen,
            Schulen,
            Kontakte,
            SchulKontakte,
            Termine,
            KontakteSchule,
            KontakteHochschule
        )
    }
}