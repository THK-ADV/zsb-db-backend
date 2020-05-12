package utilty

import mu.KLogger
import mu.KotlinLogging
import mu.Marker
import org.slf4j.Logger

/**
 * Decorate [KLogger] functions trace, info, debug, warn and error with colored versions
 */
class ColoredLogging(private val logger: KLogger = KotlinLogging.logger {}) : KLogger {
    override val underlyingLogger: Logger = logger.underlyingLogger

    // Modified functions

    override fun info(msg: String) = logger.info(ColoredStrings.lightBlue(msg))

    override fun trace(msg: String?) = logger.trace(ColoredStrings.orange(msg ?: ""))

    override fun debug(msg: String) = logger.debug(ColoredStrings.lightBlue(msg))

    override fun warn(msg: String) = logger.warn(ColoredStrings.yellow(msg))

    override fun error(msg: String) = logger.error(ColoredStrings.red(msg))


    // not modified functions

    override fun <T : Throwable> catching(throwable: T) = logger.catching(throwable)

    override fun debug(msg: () -> Any?) = logger.debug { msg }

    override fun debug(t: Throwable?, msg: () -> Any?) = logger.debug(t) { msg }

    override fun debug(marker: Marker?, msg: () -> Any?) = logger.debug(marker) { msg }

    override fun debug(marker: Marker?, t: Throwable?, msg: () -> Any?) = logger.debug(marker, t) { msg }

    override fun info(format: String?, arg: Any?) = logger.info(format, arg)

    override fun info(format: String?, arg1: Any?, arg2: Any?) = logger.info(format, arg1, arg2)

    override fun info(format: String?, vararg arguments: Any?) = logger.info(format, arguments)

    override fun info(msg: String?, t: Throwable?) = logger.info(msg, t)

    override fun info(marker: org.slf4j.Marker?, msg: String?) = logger.info(marker, msg)

    override fun info(marker: org.slf4j.Marker?, format: String?, arg: Any?) = logger.info(marker, format, arg)

    override fun info(marker: org.slf4j.Marker?, format: String?, arg1: Any?, arg2: Any?) =
        logger.info(marker, format, arg1, arg2)

    override fun info(marker: org.slf4j.Marker?, format: String?, vararg arguments: Any?) =
        logger.info(marker, format, arguments)

    override fun info(marker: org.slf4j.Marker?, msg: String?, t: Throwable?) = logger.info(marker, msg, t)

    override fun isDebugEnabled(): Boolean = logger.isDebugEnabled

    override fun isDebugEnabled(marker: org.slf4j.Marker?): Boolean = logger.isDebugEnabled(marker)

    override fun isErrorEnabled(): Boolean = logger.isErrorEnabled

    override fun isErrorEnabled(marker: org.slf4j.Marker?): Boolean = logger.isErrorEnabled(marker)

    override fun isInfoEnabled(): Boolean = logger.isInfoEnabled

    override fun isInfoEnabled(marker: org.slf4j.Marker?): Boolean = logger.isInfoEnabled(marker)

    override fun isTraceEnabled(): Boolean = logger.isTraceEnabled

    override fun isTraceEnabled(marker: org.slf4j.Marker?): Boolean = logger.isTraceEnabled(marker)

    override fun isWarnEnabled(): Boolean = logger.isWarnEnabled

    override fun isWarnEnabled(marker: org.slf4j.Marker?): Boolean = logger.isWarnEnabled(marker)

    override fun <T : Throwable> throwing(throwable: T): T = logger.throwing(throwable)

    override fun trace(msg: () -> Any?) = logger.trace(msg)

    override fun trace(t: Throwable?, msg: () -> Any?) = logger.trace(t, msg)

    override fun trace(marker: Marker?, msg: () -> Any?) = logger.trace(marker, msg)

    override fun trace(marker: Marker?, t: Throwable?, msg: () -> Any?) = logger.trace(marker, t, msg)

    override fun trace(format: String?, arg: Any?) = logger.trace(format, arg)

    override fun trace(format: String?, arg1: Any?, arg2: Any?) = logger.trace(format, arg1, arg2)

    override fun trace(format: String?, vararg arguments: Any?) = logger.trace(format, arguments)

    override fun trace(msg: String?, t: Throwable?) = logger.trace(msg, t)

    override fun trace(marker: org.slf4j.Marker?, msg: String?) = logger.trace(marker, msg)

    override fun trace(marker: org.slf4j.Marker?, format: String?, arg: Any?) = logger.trace(marker, format, arg)

    override fun trace(marker: org.slf4j.Marker?, format: String?, arg1: Any?, arg2: Any?) =
        logger.trace(marker, format, arg1, arg2)

    override fun trace(marker: org.slf4j.Marker?, format: String?, vararg argArray: Any?) =
        logger.trace(marker, format, argArray)

    override fun trace(marker: org.slf4j.Marker?, msg: String?, t: Throwable?) = logger.trace(marker, msg, t)

    override fun warn(msg: () -> Any?) = logger.warn(msg)

    override fun warn(t: Throwable?, msg: () -> Any?) = logger.warn(t, msg)

    override fun warn(marker: Marker?, msg: () -> Any?) = logger.warn(marker, msg)

    override fun warn(marker: Marker?, t: Throwable?, msg: () -> Any?) = logger.warn(marker, t, msg)

    override fun debug(format: String?, arg: Any?) = logger.debug(format, arg)

    override fun debug(format: String?, arg1: Any?, arg2: Any?) = logger.debug(format, arg1, arg2)

    override fun debug(format: String?, vararg arguments: Any?) = logger.debug(format, arguments)

    override fun debug(msg: String?, t: Throwable?) = logger.debug(msg, t)

    override fun debug(marker: org.slf4j.Marker?, msg: String?) = logger.debug(marker, msg)

    override fun debug(marker: org.slf4j.Marker?, format: String?, arg: Any?) = logger.debug(marker, format, arg)

    override fun debug(marker: org.slf4j.Marker?, format: String?, arg1: Any?, arg2: Any?) =
        logger.debug(marker, format, arg1, arg2)

    override fun debug(marker: org.slf4j.Marker?, format: String?, vararg arguments: Any?) =
        logger.debug(marker, format, arguments)

    override fun debug(marker: org.slf4j.Marker?, msg: String?, t: Throwable?) = logger.debug(marker, msg, t)

    override fun entry(vararg argArray: Any?) = logger.entry(argArray)

    override fun error(msg: () -> Any?) = logger.error(msg)

    override fun error(t: Throwable?, msg: () -> Any?) = logger.error(t, msg)

    override fun error(marker: Marker?, msg: () -> Any?) = logger.error(marker, msg)

    override fun error(marker: Marker?, t: Throwable?, msg: () -> Any?) = logger.error(marker, t, msg)

    override fun warn(format: String?, arg: Any?) = logger.warn(format, arg)

    override fun warn(format: String?, vararg arguments: Any?) = logger.warn(format, arguments)

    override fun warn(format: String?, arg1: Any?, arg2: Any?) = logger.warn(format, arg1, arg2)

    override fun warn(msg: String?, t: Throwable?) = logger.warn(msg, t)

    override fun warn(marker: org.slf4j.Marker?, msg: String?) = logger.warn(marker, msg)

    override fun warn(marker: org.slf4j.Marker?, format: String?, arg: Any?) = logger.warn(marker, format, arg)

    override fun warn(marker: org.slf4j.Marker?, format: String?, arg1: Any?, arg2: Any?) =
        logger.warn(marker, format, arg1, arg2)

    override fun warn(marker: org.slf4j.Marker?, format: String?, vararg arguments: Any?) =
        logger.warn(marker, format, arguments)

    override fun warn(marker: org.slf4j.Marker?, msg: String?, t: Throwable?) = logger.warn(marker, msg, t)

    override fun error(format: String?, arg: Any?) = logger.error(format, arg)

    override fun error(format: String?, arg1: Any?, arg2: Any?) = logger.error(format, arg1, arg2)

    override fun error(format: String?, vararg arguments: Any?) = logger.error(format, arguments)

    override fun error(msg: String?, t: Throwable?) = logger.error(msg, t)

    override fun error(marker: org.slf4j.Marker?, msg: String?) = logger.error(marker, msg)

    override fun error(marker: org.slf4j.Marker?, format: String?, arg: Any?) = logger.error(marker, format, arg)

    override fun error(marker: org.slf4j.Marker?, format: String?, arg1: Any?, arg2: Any?) =
        logger.error(marker, format, arg1, arg2)

    override fun error(marker: org.slf4j.Marker?, format: String?, vararg arguments: Any?) =
        logger.error(marker, format, arguments)

    override fun error(marker: org.slf4j.Marker?, msg: String?, t: Throwable?) = logger.error(marker, msg, t)

    override fun exit() = logger.exit()

    override fun <T> exit(result: T): T = logger.exit(result)

    override fun getName(): String = logger.name

    override fun info(msg: () -> Any?) = logger.info { msg }

    override fun info(t: Throwable?, msg: () -> Any?) = logger.info(t, msg)

    override fun info(marker: Marker?, msg: () -> Any?) = logger.info(marker, msg)

    override fun info(marker: Marker?, t: Throwable?, msg: () -> Any?) = logger.info(marker, t, msg)
}