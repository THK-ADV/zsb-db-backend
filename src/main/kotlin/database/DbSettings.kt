package database

import org.jetbrains.exposed.sql.Database


object DbSettings {
    val db by lazy {
        Database.connect(
            "jdbc:postgresql://localhost:5432/zsb",
            driver = "org.postgresql.Driver",
            user = "ktor", password = "0N61PNVEB}(^"
        )
    }
}