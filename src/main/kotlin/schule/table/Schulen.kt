package schule.table

import adresse.table.Adressen
import org.jetbrains.exposed.dao.id.IntIdTable

object Schulen : IntIdTable() {
    val schulform = integer("schulform")
    val schwerpunkt = text("schwerpunkt")
    val kooperationsvertrag = bool("kooperationsvertrag")
    val adress_id = reference("adress_id", Adressen)
    val schulleitung_mail = varchar("schulleitung_mail", 250)
    val stubo_mail = varchar("stubo_mail", 250)
    val schueleranzahl = integer("schueleranzahl")
    val kaoa_hochschule = bool("kaoa_hochschule")
    val talentscouting = bool("talentscouting")
}