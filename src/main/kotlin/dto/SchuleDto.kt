package dto

import database.Adresse
import database.Ort
import database.Schule
import kotlinx.serialization.Serializable

@Serializable
data class SchuleDto(
    val schulform: String,
    val schwerpunkt: String,
    val kooperationsvertrag: Boolean,
    val adress: AdresseDto,
    val schulleitung_mail: String, // TODO new type? useful for parsing
    val stubo_mail: String,
    val schueleranzahl: Int,
    val kaoa_hochschule: Boolean,
    val talentscouting: Boolean
) {
    companion object {
        fun convert(dto: SchuleDto): Schule {
            TODO("Implement")
        }

        fun convert(entity: Schule): SchuleDto {
            return SchuleDto(
                entity.schulform,
                entity.schwerpunkt,
                entity.kooperationsvertrag,
                AdresseDto.convert(entity.adresse),
                entity.schulleitung_mail,
                entity.stubo_mail,
                entity.schueleranzahl,
                entity.kaoa_hochschule,
                entity.talentscouting
            )
        }
    }
}

@Serializable
data class AdresseDto(
    val strasse: String,
    val hausnummer: String,
    val ort: OrtDto
) {
    companion object {
        fun convert(dto: AdresseDto) {
            TODO("Implement")
        }

        fun convert(entity: Adresse) = AdresseDto(entity.strasse, entity.hausnummer, OrtDto.convert(entity.ort))
    }
}

@Serializable
data class OrtDto(
    val plz: Int,
    val bezeichnung: String
) {
    companion object {
        fun convert(dto: OrtDto): Ort {
            TODO("Implement")
        }

        fun convert(entity: Ort) = OrtDto(entity.plz, entity.bezeichnung)
    }

}
