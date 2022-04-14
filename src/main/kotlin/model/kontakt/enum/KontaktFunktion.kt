package model.kontakt.enum

import kotlinx.serialization.Serializable

enum class KontaktFunktion(val id: Int, val desc: String) {
    KEINE(0, "Unbekannt"),
    SCHULLEITUNG(1, "Schulleitung"),
    SEKRETARIAT(2, "Sekretariat"),
    LEHRER_IN(3, "Lehrer*in"),
    ANSPRECHPARTNER_IN_TALENTSCOUTING(4, "Ansprechpartner*in Talentscouting"),
    OBERSTUFENKOORDINATOR_IN(5, "Oberstufenkoordinator*in"),
    STUDIEN_UND_BERUFSWAHLKOORDINATOR_IN(6, "Studien- und Berufswahlkoordinator*in"),
    BA_BERATER_IN(7, "BA-Berater*in"),
    JUGENDBERUFSAGENTUR(8, "Jugendberufsagentur"),
    IHK(9, "IHK"),
    HWK(10, "HWK"),
    KOMMUNALE_KOORDINIERUNG(11, "Kommunale Koordinierung"),
    SCHULAMT(12, "Schulamt"),
    UNIVERSITAET_ZU_KOELN(13, "Universität zu Köln"),
    DEUTSCHE_SPORTHOCHSCHULE(14, "Deutsche Sporthochschule"),
    UNIVERSITAET_BONN(15, "Universität Bonn"),
    FH_AACHEN(16, "FH Aachen"),
    RWTH_AACHEN(17, "RWTH Aachen"),
    HOCHSCHULE_BONN_RHEIN_SIEG(18, "Hochschule Bonn-Rhein-Sieg"),
    BEZIRKSREGIERUNG(19, "Bezirksregierung"),
    REGIONALKOORDINATOR_IN(20, "Regionalkoordinator*in"),
    BEZIRKSKOORDINATOR_IN(21, "Bezirkskoordinator*in"),
    BILDUNGSBERATUNG(22, "Bildungsberatung"),
    KOELNER_STUDIERENDENWERK(23, "Kölner Studierendenwerk"),
    ANSPRECHPARTNER_INNEN_VERTRAUENSDOZENT_INNEN_BEGABTENFOERDERWERKE(
        24,
        "Ansprechpartner*innen/Vertrauensdozent*innen Begabtenförderwerke"
    ),
    OTHER(25, "Sonstiges");

    companion object {
        fun fromDesc(desc: String) = when (desc) {
            "Unbekannt" -> KEINE
            "Schulleitung" -> SCHULLEITUNG
            "Sekretariat" -> SEKRETARIAT
            "Lehrer*in" -> LEHRER_IN
            "Ansprechpartner*in Talentscouting" -> ANSPRECHPARTNER_IN_TALENTSCOUTING
            "Oberstufenkoordinator*in" -> OBERSTUFENKOORDINATOR_IN
            "Studien- und Berufswahlkoordinator*in" -> STUDIEN_UND_BERUFSWAHLKOORDINATOR_IN
            "BA-Berater*in" -> BA_BERATER_IN
            "Jugendberufsagentur" -> JUGENDBERUFSAGENTUR
            "IHK" -> IHK
            "HWK" -> HWK
            "Kommunale Koordinierung" -> KOMMUNALE_KOORDINIERUNG
            "Schulamt" -> SCHULAMT
            "Universität zu Köln" -> UNIVERSITAET_ZU_KOELN
            "Deutsche Sporthochschule" -> DEUTSCHE_SPORTHOCHSCHULE
            "Universität Bonn" -> UNIVERSITAET_BONN
            "FH Aachen" -> FH_AACHEN
            "RWTH Aachen" -> RWTH_AACHEN
            "Hochschule Bonn-Rhein-Sieg" -> HOCHSCHULE_BONN_RHEIN_SIEG
            "Bezirksregierung" -> BEZIRKSREGIERUNG
            "Regionalkoordinator*in" -> REGIONALKOORDINATOR_IN
            "Bezirkskoordinator*in" -> BEZIRKSKOORDINATOR_IN
            "Bildungsberatung" -> BILDUNGSBERATUNG
            "Kölner Studierendenwerk" -> KOELNER_STUDIERENDENWERK
            "Ansprechpartner*innen/Vertrauensdozent*innen Begabtenförderwerke" -> ANSPRECHPARTNER_INNEN_VERTRAUENSDOZENT_INNEN_BEGABTENFOERDERWERKE
            else -> OTHER
        }
    }
}

@Serializable
data class KontaktFunktionDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<KontaktFunktionDto> = KontaktFunktion.values().map { KontaktFunktionDto(it.id, it.desc) }
    }
}