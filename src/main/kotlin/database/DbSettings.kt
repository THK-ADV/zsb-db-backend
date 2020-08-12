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
        val (user, password) = loadDbCredentials(env)

        return Database.connect(
            "jdbc:postgresql://localhost:5432/zsb",
            driver = "org.postgresql.Driver",
            user = user, password = password
        )
    }

    private fun loadDbCredentials(env: ApplicationEnvironment): Pair<String, String> {
        // read credentials from .conf
        val user = env.config.propertyOrNull("db.user")?.getString()
        val password = env.config.propertyOrNull("db.password")?.getString()

        if (user != null && password != null)
            return Pair(user, password)

        throw CouldNotLoadDbCredentialsException("Could not load database credentials, pleas make sure .conf includes valid credentials")
    }
}