package error_handling


sealed class ZsbException(override val message: String) : Exception(message)

// invalid enum ids
class SchulformNotValidException(msg: String) : ZsbException(msg)
class AnzahlSusNotValidException(msg: String) : ZsbException(msg)
class KooperationspartnerNotValidException(msg: String) : ZsbException(msg)
class AnredeNotValidException(msg: String) : ZsbException(msg)

// invalid object ids
class OrtIdNotFoundException(msg: String) : ZsbException(msg)
class AdressIdNotFoundException(msg: String) : ZsbException(msg)
class SchuleIdNotFoundException(msg: String) : ZsbException(msg)
class KontaktIdNotValidException(msg: String) : ZsbException(msg)
class InstitutionIdNotValidException(msg: String) : ZsbException(msg)
class VeranstalterIdNotValidException(msg: String) : ZsbException(msg)
class UuidNotFound(msg: String): ZsbException(msg)

// other
class CouldNotGenerateSerialLetterException(msg: String) : ZsbException(msg)
class MailNotValidException(msg: String) : ZsbException(msg)
class ToManyVeranstalterException(msg: String) : ZsbException(msg)
class NotAuthorizedException(msg: String) : ZsbException(msg)
class CouldNotParseUuidException(msg: String) : ZsbException(msg)
class InternalDbException(msg: String) : ZsbException(msg)