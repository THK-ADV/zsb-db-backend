package database

import org.jetbrains.exposed.dao.id.IntIdTable

object Orte : IntIdTable() {
    val plz = integer("plz")
    val bezeichnung = text("bezeichnung")
}

object Adressen : IntIdTable() {
    val strasse = text("strasse")
    val hausnummer = varchar("hausnummer", 5)
    val ort = reference("ort", Orte)
}

object Schulen : IntIdTable() {
    val schulform = text("schulform")
    val schwerpunkt = text("schwerpunkt")
    val kooperationsvertrag = bool("kooperationsvertrag")
    val adress_id = reference("adress_id", Adressen)
    val schulleitung_mail = varchar("schulleitung_mail", 250)
    val stubo_mail = varchar("stubo_mail", 250)
    val schueleranzahl = integer("schueleranzahl")
    val kaoa_hochschule = bool("kaoa_hochschule")
    val talentscouting = bool("talentscouting")
}