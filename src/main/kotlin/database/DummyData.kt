package database

import model.adresse.Adressen
import model.kontakt.Kontakte
import model.ort.Orte
import model.schule.SchulKontakte
import model.schule.Schulen
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun clearDatabase() {
    transaction {
        // recreate DB
        SchemaUtils.drop(SchulKontakte)
        SchemaUtils.drop(Schulen)
        SchemaUtils.drop(Adressen)
        SchemaUtils.drop(Orte)
        SchemaUtils.drop(Kontakte)

        SchemaUtils.create(Orte, Adressen, Schulen, Kontakte, SchulKontakte)
    }
}