package schule.table

import adresse.table.Adressen
import kontakt.table.Kontakte
import org.jetbrains.exposed.dao.id.IntIdTable

object Schulen : IntIdTable() {
    val schulname = text("schulname")
    val schulform = integer("schulform")
    val schwerpunkt = text("schwerpunkt")
    val anzahlSus = integer("anzahl_sus")
    val kooperationsvertrag = bool("kooperationsvertrag")
    val adress_id = reference("adress_id", Adressen)
    val kontakt_a = reference("kontakt_a", Kontakte).nullable()
    val kontakt_b = reference("kontakt_b", Kontakte).nullable()
    val stubo_kontakt = reference("stubo_kontakt", Kontakte).nullable()
    val kaoa_hochschule = bool("kaoa_hochschule")
    val talentscouting = bool("talentscouting")
}