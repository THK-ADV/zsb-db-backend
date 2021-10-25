package error_handling


sealed class ZsbException(override val message: String) : Exception(message)

// invalid enum ids
class SchoolTypeNotValidException(msg: String) : ZsbException(msg)
class AmountStudentsNotValidException(msg: String) : ZsbException(msg)
class KooperationspartnerNotValidException(msg: String) : ZsbException(msg)
class SalutationNotValidException(msg: String) : ZsbException(msg)

// invalid object ids
class CityIdNotFoundException(msg: String) : ZsbException(msg)
class AddressIdNotFoundException(msg: String) : ZsbException(msg)
class SchoolIdNotFoundException(msg: String) : ZsbException(msg)
class ContactIdNotValidException(msg: String) : ZsbException(msg)
class InstitutionIdNotValidException(msg: String) : ZsbException(msg)
class HostIdNotValidException(msg: String) : ZsbException(msg)
class UuidNotFound(msg: String): ZsbException(msg)

// other
class CouldNotGenerateSerialLetterException(msg: String) : ZsbException(msg)
class MailNotValidException(msg: String) : ZsbException(msg)
class TooManyHostsException(msg: String) : ZsbException(msg)
class NotAuthorizedException(msg: String) : ZsbException(msg)
class CouldNotParseUuidException(msg: String) : ZsbException(msg)
class InternalDbException(msg: String) : ZsbException(msg)