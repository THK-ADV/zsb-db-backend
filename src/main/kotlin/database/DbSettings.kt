package database

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import tryNonEmptyString
import tryString

object DbSettings {
    val db by lazy {
        Database.connect(
            "jdbc:postgresql://localhost:5432/postgres",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "postgres"
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

    private fun loadDbSettings(env: ApplicationEnvironment): Triple<String, String, String> =
        Triple(
            env.config.tryNonEmptyString("db.url"),
            env.config.tryNonEmptyString("db.user"),
            env.config.tryString("db.password") // DB password can be empty
        )
}