import io.ktor.server.config.*

fun ApplicationConfig.tryNonEmptyString(path: String): String =
    propertyOrNull(path)
        ?.getString()
        ?.takeIf { it.isNotEmpty() }
        ?: throw Throwable("missing path: $path")

fun ApplicationConfig.tryNonEmptyInt(path: String): Int =
    propertyOrNull(path)
        ?.getString()
        ?.toIntOrNull()
        ?.takeIf { it > 0 }
        ?: throw Throwable("missing path: $path")