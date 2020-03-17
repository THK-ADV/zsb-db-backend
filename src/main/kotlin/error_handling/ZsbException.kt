package error_handling


sealed class ZsbException(msg: String): Exception(msg)

class MailNotValidException(msg: String): ZsbException(msg)
class NotAuthorizedException(msg: String) : ZsbException(msg)