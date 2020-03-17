package ort.table

import org.jetbrains.exposed.dao.id.IntIdTable

object Orte : IntIdTable() {
    val plz = integer("plz")
    val bezeichnung = text("bezeichnung")
}
