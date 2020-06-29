package legacy_import

object SchuleIndices {
    const val schulname = 0
    const val nameStuBo = 1 // multiple people possible, split by 'und'
    const val mailKontaktPerson = 2 // multiple people possible, split by 'und'
    const val Schulform = 3
    const val Kooperationsvertrag = 4 // empty == false
    const val nummer_strasse = 5 // split by comma
    const val plz_ort = 6 // split by comma
    const val regierungsbezirk = 7
    const val kreis = 8
    const val kontaktPerson2 = 9 // multiple people possible - split by 'und' with funktion in brackets ()
    const val mailKontaktPerson2 = 10 // multiple mail possible split by 'und'
    const val nameStuBO2 = 11 // multiple people possible split by 'und'
    const val mailStuBO = 12 //  multiple people possible split by 'und'
    const val anzahlSuS = 13
    const val kAoA = 14
    const val talentscout = 15
}