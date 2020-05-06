package error_handling


sealed class ZsbException(override val message: String) : Exception(message)

class MailNotValidException(msg: String) : ZsbException(msg)
class NotAuthorizedException(msg: String) : ZsbException(msg)
class SchulformNotValidException(msg: String) : ZsbException(msg)