package model.schule

import error_handling.*
import kotlinx.serialization.Serializable
import model.address.Adresse
import model.address.AdresseDto
import model.address.Adressen
import model.kontakt.Kontakt
import model.kontakt.KontaktDao
import model.kontakt.KontaktDto
import model.kontakt.Kontakte
import model.schule.enum.Kooperationspartner
import model.schule.enum.Schulform
import model.termin.Termine
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object Schulen : UUIDTable() {
    val name = text("name")
    val type = integer("form")
    val comment = text("kommentar")
    val amountStudents11 = integer("schuelerzahl_11")
    val amountStudents12 = integer("schuelerzahl_12")
    val amountStudents13 = integer("schuelerzahl_13")
    val address = reference("adress_id", Adressen)
    val phonenumber = text("telefon")
    val email = text("email")
    val website = text("website")
    val cooperationpartner = integer("kooperationspartner")
    val kaoaSupervisor = integer("kaoa_supervisor")
    val talentscout = integer("talentscout")
    val cooperationcontract = bool("kooperationsvertrag")
}

// many to many reference
object SchulKontakte : Table() {
    val school = reference("model/schule", Schulen)
    val contact = reference("model/kontakt", Kontakte)
    override val primaryKey = PrimaryKey(school, contact, name = "PK_SchulKontakte")
}

class Schule(uuid: EntityID<UUID>) : UUIDEntity(uuid) {

    private var name by Schulen.name
    private var type by Schulen.type
    private var comment by Schulen.comment
    private var amountStudents11 by Schulen.amountStudents11
    private var amountStudents12 by Schulen.amountStudents12
    private var amountStudents13 by Schulen.amountStudents13
    private var address by Adresse referencedOn Schulen.address
    private var phonenumber by Schulen.phonenumber
    private var email by Schulen.email
    private var website by Schulen.website
    private var cooperationpartner by Schulen.cooperationpartner
    private var kaoaSupervisor by Schulen.kaoaSupervisor
    private var talentscout by Schulen.talentscout
    private var cooperationcontract by Schulen.cooperationcontract
    private var contacts by Kontakt via SchulKontakte

    companion object : UUIDEntityClass<Schule>(Schulen) {
        /**
         * persist in db (create or update)
         */
        fun save(dto: SchuleDto): Result<Schule> {
            val exception = validateDto(dto)
            if (exception != null) return Result.failure(exception)

            return transaction {
                // fetch Adresse
                val adresse = anyOrNull { Adresse[UUID.fromString(dto.address_id)] }
                    ?: return@transaction Result.failure(AddressIdNotFoundException("Could not find Adresse with ID: ${dto.address_id}"))

                // create or update Schule
                val schule: Schule = if (dto.id == null) new(UUID.randomUUID()) {
                    // note: manual creation of the uuid is needed to save the intermediate table in one transaction
                    update(dto, adresse)
                } else {
                    // parse UUID
                    val uuid = anyOrNull { UUID.fromString(dto.id) }
                        ?: return@transaction Result.failure(CouldNotParseUuidException("Could parse UUID: ${dto.id}"))

                    // fetch current Schule
                    val aktuelleSchule = anyOrNull { Schule[uuid] }
                        ?: return@transaction Result.failure(SchoolIdNotFoundException("Could not find Schule with ID: $uuid"))

                    // update Schule
                    aktuelleSchule.update(dto, adresse)

                    // fetch updated Schule
                    Schule[uuid]
                }

                // return updated or created Schule
                Result.success(schule)
            }
        }

        fun delete(schuleId: UUID): Boolean {
            val result = anyOrNull {

                transaction {
                    Termine.deleteWhere { Termine.school_id eq schuleId }
                    SchulKontakte.deleteWhere { SchulKontakte.school eq schuleId }
                    Schulen.deleteWhere { Schulen.id eq schuleId }
                }
            }

            return result != null
        }

        private fun validateDto(dto: SchuleDto): ZsbException? {
            if (!Kooperationspartner.values().any { it.id == dto.cooperationpartner })
                return CooperationPartnerNotValidException("This is not a valid index for Kooperationspartner.")

            // valid id for AnzahlSus?
            if (!(dto.amount_students11 in 0..150 &&
                        dto.amount_students12 in 0..150 &&
                        dto.amount_students13 in 0..150)
            ) {
                return AmountStudentsNotValidException("This is not a valid index for AnzahlSus.")
            }

            // valid id for schulform?
            if (Schulform.getDescById(dto.type) == null)
                return SchoolTypeNotValidException("This is not a valid index for Schulform.")

            return null
        }
    }

    private fun update(dto: SchuleDto, adresse: Adresse) {
        this.name = dto.name
        this.type = dto.type
        this.comment = dto.comment
        this.amountStudents11 = dto.amount_students11
        this.amountStudents12 = dto.amount_students12
        this.amountStudents13 = dto.amount_students13
        this.address = adresse
        this.phonenumber = dto.phonenumber
        this.email = dto.email
        this.website = dto.website
        this.cooperationpartner = dto.cooperationpartner
        this.kaoaSupervisor = dto.kaoaSupervisor
        this.talentscout = dto.talentscout
        this.cooperationcontract = dto.cooperationcontract
        this.contacts = SizedCollection(KontaktDao.getAllById(dto.contacts_ids))
    }

    fun toDto() = SchuleDto(
        id.value.toString(),
        name,
        type,
        comment,
        amountStudents11,
        amountStudents12,
        amountStudents13,
        phonenumber,
        email,
        website,
        cooperationpartner,
        kaoaSupervisor,
        talentscout,
        cooperationcontract,
        address.id.value.toString(),
        null,
        contacts.map { it.id.value.toString() },
        listOf()
    )

    fun toAtomicDto() = SchuleDto(
        id.value.toString(),
        name,
        type,
        comment,
        amountStudents11,
        amountStudents12,
        amountStudents13,
        phonenumber,
        email,
        website,
        cooperationpartner,
        kaoaSupervisor,
        talentscout,
        cooperationcontract,
        address.id.value.toString(),
        address.toDto(),
        contacts.map { it.id.value.toString() },
        contacts.map { it.toDto() }
    )
}

@Serializable
data class SchuleDto(
    val id: String? = null,
    val name: String,
    val type: Int,
    val comment: String,
    val amount_students11: Int,
    val amount_students12: Int,
    val amount_students13: Int,
    val phonenumber: String,
    val email: String,
    val website: String,
    val cooperationpartner: Int,
    val kaoaSupervisor: Int,
    val talentscout: Int,
    val cooperationcontract: Boolean,
    val address_id: String,
    val address: AdresseDto? = null,
    val contacts_ids: List<String> = listOf(),
    val contacts: List<KontaktDto> = listOf()
)