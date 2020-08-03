package schule

import adresse.Adresse
import adresse.Adressen
import error_handling.*
import kontakt.Kontakt
import kontakt.KontaktDao
import kontakt.Kontakte
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.fromTry
import java.util.*

object Schulen : UUIDTable() {
    val schulname = text("schulname")
    val schulform = integer("schulform")
    val schwerpunkt = text("schwerpunkt")
    val anzahlSus = integer("anzahl_sus")
    val kooperationsvertrag = bool("kooperationsvertrag")
    val adress_id = reference("adress_id", Adressen)
    val kaoa_hochschule = bool("kaoa_hochschule")
    val talentscouting = bool("talentscouting")
}

// many to many reference
object SchulKontakte : Table() {
    val schule = reference("schule", Schulen)
    val kontakt = reference("kontakt", Kontakte)
    override val primaryKey = PrimaryKey(schule, kontakt, name = "PK_SchulKontakte")
}

class Schule(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    var schulname by Schulen.schulname
    var schulform by Schulen.schulform
    var schwerpunkt by Schulen.schwerpunkt
    var anzahlSus by Schulen.anzahlSus
    var kooperationsvertrag by Schulen.kooperationsvertrag
    var adresse by Adresse referencedOn Schulen.adress_id
    var kontakte by Kontakt via SchulKontakte
    var kaoaHochschule by Schulen.kaoa_hochschule
    var talentscouting by Schulen.talentscouting

    companion object : UUIDEntityClass<Schule>(Schulen) {
        /**
         * persist in db (create or update)
         */
        fun save(dto: SchuleDto): Result<Schule> {
            val exception = validateDto(dto)
            if (exception != null) return Result.failure(exception)

            return transaction {
                // fetch Adresse
                val adresse = fromTry { Adresse[dto.adress_id] }
                    ?: return@transaction Result.failure(AdressIdNotFoundException("Could not find Adresse with ID: ${dto.adress_id}"))

                // create or update Schule
                val schule: Schule = if (dto.schule_id == null) new(UUID.randomUUID()) {
                    // note: manual creation of the uuid is needed to save the intermediate table in one transaction
                    update(dto, adresse)
                } else {
                    // parse UUID
                    val uuid = fromTry { UUID.fromString(dto.schule_id) }
                        ?: return@transaction Result.failure(CouldNotParseUuidException("Could parse UUID: ${dto.schule_id}"))

                    // fetch current Schule
                    val currentSchule = fromTry { Schule[uuid] }
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

        private fun validateDto(dto: SchuleDto): ZsbException? {
            // TODO validate anzahlSUS
            // TODO validate kontaktUUIDs?
            if (Schulform.getDescById(dto.schulform) == null)
                return SchulformNotValidException("This is not a valid index for Schulform.")

            return null
        }
    }

    private fun update(dto: SchuleDto, adresse: Adresse) {
        this.schulname = dto.name
        this.schulform = dto.schulform
        this.schwerpunkt = dto.schwerpunkt.toString() // TODO find better solution. Null values in DB?
        this.anzahlSus = dto.anzahl_sus
        this.kooperationsvertrag = dto.kooperationsvertrag
        this.adresse = adresse
        this.kaoaHochschule = dto.kaoa_hochschule
        this.talentscouting = dto.talentscouting
        this.kontakte = SizedCollection(KontaktDao.getAllById(dto.kontakte_ids))
    }

    fun toDto() = SchuleDto(
        id.value.toString(),
        schulname,
        schulform,
        schwerpunkt,
        anzahlSus,
        kooperationsvertrag,
        adresse.id.value,
        kontakte.map { it.id.value.toString() },
        kaoaHochschule,
        talentscouting
    )
}