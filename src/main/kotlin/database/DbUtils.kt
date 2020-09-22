package database

import model.adresse.AdresseDao
import model.adresse.Adressen
import model.bericht.Bericht
import model.bericht.BerichtDto
import model.bericht.Berichte
import model.institution.Institution
import model.institution.InstitutionDto
import model.institution.Institutionen
import model.kontakt.KontaktDao
import model.kontakt.Kontakte
import model.ort.Orte
import model.schule.SchulKontakte
import model.schule.Schulen
import model.schule.enum.AnzahlSus
import model.veranstalter.Veranstalter
import model.veranstalter.VeranstalterDto
import model.veranstalter.VeranstalterTable
import model.veranstaltung.Veranstaltung
import model.veranstaltung.VeranstaltungDto
import model.veranstaltung.Veranstaltungen
import model.veranstaltung.enum.Kategorie
import model.veranstaltung.enum.Stufe
import model.veranstaltung.enum.Vortragsart
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.ColoredLogging
import java.time.LocalDateTime

fun recreateDatabase() {
    transaction {
        // recreate DB
        SchemaUtils.drop(Berichte)
        SchemaUtils.drop(Veranstaltungen)
        SchemaUtils.drop(VeranstalterTable)
        SchemaUtils.drop(Institutionen)
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
            Institutionen,
            VeranstalterTable,
            Veranstaltungen,
            Berichte
        )
    }
}

/**
 * Generates dummy data for, Berichte, Veranstaltungen, Veranstalter und Institutionen
 */
fun generateDummyData() {
    val adressen = AdresseDao.getAll(true)
    val kontakte = KontaktDao.getAll()

    val institutionResult = Institution.save(
        InstitutionDto(
            null,
            "Otto Fuchs",
            adressen.first().adress_id ?: "",
            kontakte.first().uuid ?: ""
        )
    )

    val veranstalterResult = Veranstalter.save(
        VeranstalterDto(
            null,
            null,
            institutionResult.getOrNull()?.id?.value.toString()
        )
    )

    val veranstaltungResult = Veranstaltung.save(
        VeranstaltungDto(
            null,
            "Neue Felgen für Porsche",
            veranstalterResult.getOrNull()?.id?.value.toString(),
            Kategorie.VORTRAG.id,
            "Felgengußtechniken",
            Vortragsart.SONSTIGES.id,
            LocalDateTime.now().toString(),
            kontakte.last().uuid ?: "",
            AnzahlSus.D.id,
            Stufe.UNKNOWN.id,
            "1. Begrüßung\n2. Vortrag\n3. Fragen\n4. Party",
            "k.A."
        )
    )

    val berichtResult = Bericht.save(
        BerichtDto(
            null,
            "Erste Eindrücke",
            "Der Vortrag war sehr interessant und könnte eine Möglichkeit bieten junge Menschen für den Ingenieursberuf zu begeistern.",
            veranstaltungResult.getOrNull()?.id?.value.toString()
        )
    )
    val bericht = berichtResult.getOrNull()
    if (bericht == null) {
        ColoredLogging.LOG.warn("Could not load dummy data.")
    }
}