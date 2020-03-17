package adresse.table

import org.jetbrains.exposed.dao.id.IntIdTable
import ort.table.Orte

object Adressen : IntIdTable() {
    val strasse = text("strasse")
    val hausnummer = varchar("hausnummer", 5)
    val ort = reference("ort", Orte)
}
