package word.enum

import RESOURCE_PATH
import kotlinx.serialization.Serializable


enum class ZsbSignatur(val id: Int, val desc: String, val text: String, val path: String) {
    DEMO(1, "JFK-Demo", "Best regards\nJ. F. Kennedy\n---\n", RESOURCE_PATH + "demo-sign.jpeg");


}

@Serializable
data class ZsbSignaturDto(val id: Int, val desc: String, val text: String, val path: String) {
    companion object {
        fun generate(): List<ZsbSignaturDto> =
            ZsbSignatur.values().map { ZsbSignaturDto(it.id, it.desc, it.text, it.path) }
    }
}
