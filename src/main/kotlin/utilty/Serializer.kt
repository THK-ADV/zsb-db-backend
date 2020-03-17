package utilty

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

object Serializer {
    val stable = Json(JsonConfiguration.Stable)
}