package legacy_import

import java.io.File

object ImportLog {
    private val file = File("src\\main\\resources\\log.txt")

    fun error(msg: String) {
        file.appendText("ERROR: $msg\n")
    }

    fun info(msg: String) {
        file.appendText("INFO: $msg\n")
    }
}