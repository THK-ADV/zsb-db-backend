import io.ktor.server.application.*

data class MailSettings(val sender: String, val host: String, val timeout: Int, val chunkSize: Int) {
    companion object {
        fun fromEnvironment(env: ApplicationEnvironment): MailSettings =
            MailSettings(
                env.config.tryNonEmptyString("mail.sender"),
                env.config.tryNonEmptyString("mail.host"),
                env.config.tryNonEmptyInt("mail.timeout"),
                env.config.tryNonEmptyInt("mail.chunkSize")
            )
    }
}

