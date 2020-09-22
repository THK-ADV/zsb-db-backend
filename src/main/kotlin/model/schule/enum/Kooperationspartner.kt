package model.schule.enum

import kotlinx.serialization.Serializable

enum class Kooperationspartner(val id: Int, val desc: String) {
    TH(0, "TH Köln"),
    UNI(1, "Uni Köln")
}

@Serializable
data class KooperationspartnerDto(val id: Int, val desc: String) {
    companion object {
        fun generate(): List<KooperationspartnerDto> = Kooperationspartner.values().map {
            KooperationspartnerDto(it.id, it.desc)
        }
    }
}