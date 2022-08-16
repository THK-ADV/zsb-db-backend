package database

import model.address.Adressen
import model.bericht.Berichte
import model.institution.Institutionen
import model.kaoaarbeit.KAoAArbeiten
import model.kontakt.Kontakte
import model.ort.Orte
import model.schule.SchulKontakte
import model.schule.Schulen
import model.veranstalter.VeranstalterTable
import model.termin.Termine
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun recreateDatabase() {
    transaction {
        // recreate DB
        SchemaUtils.drop(Berichte)
        SchemaUtils.drop(Termine)
        SchemaUtils.drop(VeranstalterTable)
        SchemaUtils.drop(Institutionen)
        SchemaUtils.drop(SchulKontakte)
        SchemaUtils.drop(KAoAArbeiten)
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
            Termine,
            Berichte,
            KAoAArbeiten
        )
    }
}

/**
 * Generates dummy data for, Berichte, Termine, Veranstalter und Institutionen
 */
/*fun generateDummyData() {
    val adressen = AdresseDao.getAll(true)

    val institutionResult = Institution.save(
        InstitutionDto(
            null,
            "Otto Fuchs",
            adressen.first().adress_id ?: "",
            "test@mail.net"
        )
    )

    val veranstalterResult = Veranstalter.save(
        VeranstalterDto(
            null,
            null,
            institutionResult.getOrNull()?.id?.value.toString()
        )
    )

    val terminResult = Termin.save(
        TerminDto(
            null,
            "Neue Felgen für Porsche",
            veranstalterResult.getOrNull()?.id?.value.toString(),
            listOf(Kategorie.VORTRAG_TH.id),
            "Felgengußtechniken",
            LocalDateTime.now().toString(),
            "Anzahl sus",
            listOf(Stufe.UNKNOWN.id),
            "1. Begrüßung\n2. Vortrag\n3. Fragen\n4. Party",
            "k.A.",
            "Hans Peter"
        )
    )

    Termin.save(
        TerminDto(
            null,
            "Studienberatung",
            veranstalterResult.getOrNull()?.id?.value.toString(),
            listOf(Kategorie.WORKSHOP_EXTERN.id),
            "Studienmöglichkeiten an der TH Köln",
            LocalDateTime.now().toString(),
            "Anzahl SUS: 100k",
            listOf(Stufe.UNKNOWN.id),
            "1. Begrüßung\n2. Vortrag\n3. Fragen\n4. Party",
            "k.A.",
            "Fridolin Fröhlich"
        )
    )

    val berichtResult = Bericht.save(
        BerichtDto(
            null,
            "Erste Eindrücke",
            "Der Vortrag war sehr interessant und könnte eine Möglichkeit bieten junge Menschen für den Ingenieursberuf zu begeistern.",
            terminResult.getOrNull()?.id?.value.toString()
        )
    )
    val bericht = berichtResult.getOrNull()
    if (bericht == null) {
        ColoredLogging.LOG.warn("Could not load dummy data.")
    }
}*/