package adresse.table

import org.jetbrains.exposed.dao.id.IntIdTable
import ort.table.Orte

object Adressen : IntIdTable() {
    val strasse = varchar("strasse", 250)
    val hausnummer = varchar("hausnummer", 20)
    val ort = reference("ort", Orte)
}
