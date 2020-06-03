package ort.table

import org.jetbrains.exposed.dao.id.IntIdTable

object Orte : IntIdTable() {
    val regierungsbezirk = text("regierungsbezirk")
    val kreis = text("kreis")
    val plz = integer("plz")
    val bezeichnung = text("bezeichnung")
}
