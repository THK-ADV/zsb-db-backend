package utilty

fun <T> List<T>.get(condition: (T) -> Boolean): T? {
    this.forEach {
        if (condition(it)) return it
    }

    return null
}

fun <T> List<T>.contains(condition: (T) -> Boolean): Boolean {
    this.forEach {
        if (condition(it)) return true
    }

    return false
}


fun <T> MutableList<T>.addDistinct(value: T): T {
    val a = this.indexOf(value)
    return if (a < 0) {
        this.add(value); value
    } else this[a]
}