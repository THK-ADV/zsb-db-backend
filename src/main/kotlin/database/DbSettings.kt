package database

import io.ktor.application.ApplicationEnvironment
import org.jetbrains.exposed.sql.Database

class CouldNotLoadDbCredentialsException(override val message: String) : Exception(message)

object DbSettings {
    val db by lazy {
        Database.connect(
            "jdbc:postgresql://localhost:5432/zsb",
            driver = "org.postgresql.Driver",
            user = "ktor", password = "0N61PNVEB}(^"
        )
    }

    fun connect(env: ApplicationEnvironment): Database {
        val (url, user, password) = loadDbSettings(env)

        return Database.connect(
            url,
            driver = "org.postgresql.Driver",
            user = user,
            password = password
        )
    }

    private fun loadDbSettings(env: ApplicationEnvironment): Triple<String, String, String> {
        // read credentials from .conf
        val url = env.config.propertyOrNull("db.url")?.getString()
        val user = env.config.propertyOrNull("db.user")?.getString()
        val password = env.config.propertyOrNull("db.password")?.getString()

        when {
            user != null && password != null && url != null ->
                return Triple(url, user, password)
            else ->
                throw CouldNotLoadDbCredentialsException("Could not load database credentials, pleas make sure .conf includes valid credentials")
        }

    }
}