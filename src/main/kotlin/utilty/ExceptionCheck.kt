package utilty

/**
 * execute an given function [f] and catch thrown exceptions.
 * @return null if [f] throws an exception or [T] if not
 */
fun <T> anyOrNull(f: () -> T): T? {
    return try {
        f()
    } catch (e: Exception) {
//        e.printStackTrace()
        null
    }
}