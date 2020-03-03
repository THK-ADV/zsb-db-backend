package database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Ort(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Ort>(Orte)

    var plz by Orte.plz
    var bezeichnung by Orte.bezeichnung
}

class Adresse(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Adresse>(Adressen)

    var strasse by Adressen.strasse
    var hausnummer by Adressen.hausnummer
    var ort by Ort referencedOn Adressen.ort
}

class Schule(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Schule>(Schulen)

    var schulform by Schulen.schulform
    var schwerpunkt by Schulen.schwerpunkt
    var kooperationsvertrag by Schulen.kooperationsvertrag
    var adresse by Adresse referencedOn Schulen.adress_id
    var schulleitung_mail by Schulen.schulleitung_mail
    var stubo_mail by Schulen.stubo_mail
    var schueleranzahl by Schulen.schueleranzahl
    var kaoa_hochschule by Schulen.kaoa_hochschule
    var talentscouting by Schulen.talentscouting
}