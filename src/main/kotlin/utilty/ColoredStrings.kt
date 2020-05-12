package utilty

object ColoredStrings {
    //    "\u001b["  // Prefix - see [1]
    //    + "0"        // Brightness
    //    + ";"        // Separator
    //    + "31"       // Red foreground
    //    + "m"        // Suffix
    //    + text       // the text to output
    //    + "\u001b[m " // Prefix + Suffix to reset color

    fun red(text: String): String =
        "\u001b[0;31m$text\u001b[m"

    fun orange(text: String): String =
        "\u001b[0;33m$text\u001b[m"

    fun blue(text: String): String =
        "\u001b[0;34m$text\u001b[m"

    fun lightBlue(text: String): String =
        "\u001b[0;36m$text\u001b[m"

    fun black(text: String): String =
        "\u001b[0;37m$text\u001b[m"

    fun yellow(text: String): String =
        "\u001b[0;93m$text\u001b[m"

    fun whiteBorder(text: String): String =
        "\u001b[0;51m$text\u001b[m"
}