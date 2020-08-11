package error_handling


sealed class ZsbException(override val message: String) : Exception(message)

class MailNotValidException(msg: String) : ZsbException(msg)
class NotAuthorizedException(msg: String) : ZsbException(msg)
class SchulformNotValidException(msg: String) : ZsbException(msg)
class OrtIdNotFoundException(msg: String) : ZsbException(msg)
class AdressIdNotFoundException(msg: String) : ZsbException(msg)
class SchuleIdNotFoundException(msg: String) : ZsbException(msg)
class CouldNotParseUuidException(msg: String) : ZsbException(msg)
class AnzahlSusNotValidException(msg: String) : ZsbException(msg)
class KontakteIdsNotValidException(msg: String) : ZsbException(msg)