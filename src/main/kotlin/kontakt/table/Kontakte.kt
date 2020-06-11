package kontakt.table

import org.jetbrains.exposed.dao.id.UUIDTable

object Kontakte : UUIDTable() {
    val name = text("name")
    val email = text("email")
    val funktion = integer("funktion")
}