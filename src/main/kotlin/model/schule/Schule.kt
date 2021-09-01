package model.schule

import error_handling.*
import kotlinx.serialization.Serializable
import model.adresse.Adresse
import model.adresse.AdresseDto
import model.adresse.Adressen
import model.kontakt.Kontakt
import model.kontakt.KontaktDao
import model.kontakt.KontaktDto
import model.kontakt.Kontakte
import model.schule.enum.AnzahlSus
import model.schule.enum.Kooperationspartner
import model.schule.enum.Schulform
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
    val schoolname = text("schulname")
    val schooltype = integer("schulform")
    val focus = text("schwerpunkt")
    val amountStudents = integer("anzahl_sus")
    val cooperationcontract = bool("kooperationsvertrag")
    val address_id = reference("adress_id", Adressen)
    val kaoa_university = bool("kaoa_hochschule")
    val kaoa_partner = integer("kaoa_partner")
    val talentscouting = bool("talentscouting")
    val talentscouting_partner = integer("talentscouting_partner")
}

// many to many reference
object SchulKontakte : Table() {
    val schule = reference("model/schule", Schulen)
    val kontakt = reference("model/kontakt", Kontakte)
    override val primaryKey = PrimaryKey(schule, kontakt, name = "PK_SchulKontakte")
}

class Schule(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var schoolname by Schulen.schoolname
    private var schooltype by Schulen.schooltype
    private var focus by Schulen.focus
    private var amountStudents by Schulen.amountStudents
    private var cooperationcontract by Schulen.cooperationcontract
    private var address by Adresse referencedOn Schulen.address_id
    private var contacts by Kontakt via SchulKontakte
    private var kaoaUniversity by Schulen.kaoa_university
    private var kaoaPartner by Schulen.kaoa_partner
    private var talentscouting by Schulen.talentscouting
    private var talentscoutingPartner by Schulen.talentscouting_partner

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
                    ?: return@transaction Result.failure(AdressIdNotFoundException("Could not find Adresse with ID: ${dto.address_id}"))

                // create or update Schule
                val schule: Schule = if (dto.school_id == null) new(UUID.randomUUID()) {
                    // note: manual creation of the uuid is needed to save the intermediate table in one transaction
                    update(dto, adresse)
                } else {
                    // parse UUID
                    val uuid = anyOrNull { UUID.fromString(dto.school_id) }
                        ?: return@transaction Result.failure(CouldNotParseUuidException("Could parse UUID: ${dto.school_id}"))

                    // fetch current Schule
                    val currentSchule = anyOrNull { Schule[uuid] }
                        ?: return@transaction Result.failure(SchuleIdNotFoundException("Could not find Schule with ID: $uuid"))

                    // update Schule
                    currentSchule.update(dto, adresse)

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
                    SchulKontakte.deleteWhere { SchulKontakte.schule eq schuleId }
                    Schulen.deleteWhere { Schulen.id eq schuleId }
                }
            }

            return result != null
        }

        private fun validateDto(dto: SchuleDto): ZsbException? {
            val kooperationspartnerRange = (-1 until Kooperationspartner.values().count())
            if (!kooperationspartnerRange.contains(dto.kaoa_partner)
                || !kooperationspartnerRange.contains(dto.talentscouting_partner)
            ) {
                return KooperationspartnerNotValidException("This is not a valid index for Kooperationspartner.")
            }

            // valid id for AnzahlSus?
            if (!(0 until AnzahlSus.values().count()).contains(dto.amount_students)) {
                return AnzahlSusNotValidException("This is not a valid index for AnzahlSus.")
            }

            // validate kontaktUUIDs?
            anyOrNull { KontaktDao.getAllById(dto.contacts_ids) }
                ?: return KontaktIdNotValidException("kontakte_ids contains non existing kontakt_ids")

            // valid id for schulform?
            if (Schulform.getDescById(dto.schooltype) == null)
                return SchulformNotValidException("This is not a valid index for Schulform.")

            return null
        }
    }

    private fun update(dto: SchuleDto, adresse: Adresse) {
        this.schoolname = dto.name
        this.schooltype = dto.schooltype
        this.focus = dto.focus.toString()
        this.amountStudents = dto.amount_students
        this.cooperationcontract = dto.cooperationcontract
        this.address = adresse
        this.kaoaUniversity = dto.kaoa_university
        this.kaoaPartner = dto.kaoa_partner
        this.talentscouting = dto.talentscouting
        this.talentscoutingPartner = dto.talentscouting_partner
        this.contacts = SizedCollection(KontaktDao.getAllById(dto.contacts_ids))
    }

    fun toDto() = SchuleDto(
        id.value.toString(),
        schoolname,
        schooltype,
        focus,
        amountStudents,
        cooperationcontract,
        address.id.value.toString(),
        contacts.map { it.id.value.toString() },
        kaoaUniversity,
        kaoaPartner,
        talentscouting,
        talentscoutingPartner
    )

    fun toAtomicDto() = SchuleDto(
        id.value.toString(),
        schoolname,
        schooltype,
        focus,
        amountStudents,
        cooperationcontract,
        address.id.value.toString(),
        contacts.map { it.id.value.toString() },
        kaoaUniversity,
        kaoaPartner,
        talentscouting,
        talentscoutingPartner,
        contacts.map { it.toDto() },
        address.toAtomicDto()
    )
}

@Serializable
data class SchuleDto(
    val school_id: String? = null,
    val name: String,
    val schooltype: Int,
    val focus: String?,
    val amount_students: Int,
    val cooperationcontract: Boolean,
    val address_id: String,
    val contacts_ids: List<String> = listOf(),
    val kaoa_university: Boolean,
    val kaoa_partner: Int,
    val talentscouting: Boolean,
    val talentscouting_partner: Int,
    val contacts: List<KontaktDto> = listOf(),
    val address: AdresseDto? = null
)