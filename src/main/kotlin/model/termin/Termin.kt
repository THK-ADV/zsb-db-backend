package model.termin

import error_handling.CouldNotParseUuidException
import error_handling.InstitutionIdNotValidException
import error_handling.UuidNotFound
import kotlinx.serialization.Serializable
import model.schule.Schule
import model.schule.SchuleDto
import model.schule.Schulen
import model.termin.enum.*
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import utilty.anyOrNull
import java.util.*

object Termine : UUIDTable() {
    val schoolyear = text("schuljahr")
    val date = text("datum")
    val contact_school = text("kontaktperson schule")
    val contact_university = text("kontaktperson hochschule")
    val other = text("freitextfeld")
    val school_id = reference("schule_id", Schulen)
    val category = integer("terminart")
    val internCategory = integer("bei uns typ").nullable()
    val schoolCategory = integer("an schule typ").nullable()
    val kAoACategory = integer("kaoa typ").nullable()
    val talentscoutCategory = integer("talentscout typ").nullable()
    val thSpecificCategory = integer("th spezifisch typ").nullable()
    val isIndividualAppt = bool("ist einzeltermin").nullable()
    val runs = integer("durchläufe").nullable()
}

class Termin(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var schoolyear by Termine.schoolyear
    private var date by Termine.date
    private var contact_school by Termine.contact_school
    private var contact_university by Termine.contact_university
    private var other by Termine.other
    private var school by Schule referencedOn Termine.school_id
    private var category by Termine.category
    private var internCategory by Termine.internCategory
    private var schoolCategory by Termine.schoolCategory
    private var kAoACategory by Termine.kAoACategory
    private var talentscoutCategory by Termine.talentscoutCategory
    private var thSpecificCategory by Termine.thSpecificCategory
    private var isIndividualAppt by Termine.isIndividualAppt
    private var runs by Termine.runs

    companion object : UUIDEntityClass<Termin>(Termine) {
        fun save(dto: TerminDto): Result<Termin> = transaction {
            // validate ids
            val schulId = anyOrNull { UUID.fromString(dto.school_id) }
                ?: return@transaction Result.failure(
                    CouldNotParseUuidException("hochschul_id for Termin not valid.")
                )
            // get related objects from db
            val schule = anyOrNull { Schule[schulId] }
                ?: return@transaction Result.failure(
                    InstitutionIdNotValidException("UUID for hochschule is not valid")
                )

            // finding a matched Termin is skipped here due to the unique

            // update/create Termin
            val termin = if (dto.uuid != null) {
                val uuid = anyOrNull { UUID.fromString(dto.uuid) }
                    ?: return@transaction Result.failure(CouldNotParseUuidException("UUID for Termin is not valid."))
                val old = anyOrNull { Termin[uuid] }
                    ?: return@transaction Result.failure(UuidNotFound("Couldn't find Termin with UUID: $uuid"))

                // update and safe
                old.update(dto, schule)
                Termin[uuid]
            } else {
                new { update(dto, schule) }
            }

            // return result
            Result.success(termin)
        }

        /**
         * Delete termin with [id] and all attached [Berichte]
         */
        fun delete(id: UUID): Boolean {
            val result = anyOrNull {
                transaction {
                    Termine.deleteWhere { Termine.id eq id }
                }
            }

            return result != null
        }
    }

    private fun update(dto: TerminDto, schule: Schule) {
        this.schoolyear = dto.schoolyear
        this.date = dto.date
        this.contact_school = dto.contact_school
        this.contact_university = dto.contact_university
        this.other = dto.other
        this.school = schule
        this.category = dto.category.id
        this.internCategory = dto.internCategory?.id
        this.schoolCategory = dto.schoolCategory?.id
        this.kAoACategory = dto.kAoACategory?.id
        this.talentscoutCategory = dto.talentscoutCategory?.id
        this.thSpecificCategory = dto.thSpecificCategory?.id
        this.isIndividualAppt = dto.isIndividualAppt
        this.runs = dto.runs
    }

    fun toDto() = TerminDto(
        id.value.toString(),
        schoolyear,
        date,
        contact_school,
        contact_university,
        other,
        school.id.value.toString(),
        null,
        Kategorie.getById(category),
        internCategory?.let { BeiUnsTyp.getById(it) },
        schoolCategory?.let { AnSchuleTyp.getById(it) },
        kAoACategory?.let { KAoATyp.getById(it) },
        talentscoutCategory?.let { TalentscoutTyp.getById(it) },
        thSpecificCategory?.let { THSpezifischTyp.getById(it) },
        isIndividualAppt,
        runs
    )

    fun toAtomicDto() = TerminDto(
        id.value.toString(),
        schoolyear,
        date,
        contact_school,
        contact_university,
        other,
        school.id.value.toString(),
        school.toDto(),
        Kategorie.getById(category),
        internCategory?.let { BeiUnsTyp.getById(it) },
        schoolCategory?.let { AnSchuleTyp.getById(it) },
        kAoACategory?.let { KAoATyp.getById(it) },
        talentscoutCategory?.let { TalentscoutTyp.getById(it) },
        thSpecificCategory?.let { THSpezifischTyp.getById(it) },
        isIndividualAppt,
        runs
    )

    fun toTermin() = when (category) {
        1 -> AnSchuleTermin(
            id.value.toString(),
            schoolyear,
            date,
            contact_school,
            contact_university,
            other,
            school.id.value.toString(),
            school.toDto(),
            schoolCategory?.let { AnSchuleTyp.getById(it) },
            kAoACategory?.let { KAoATyp.getById(it) },
            talentscoutCategory?.let { TalentscoutTyp.getById(it) },
            thSpecificCategory?.let { THSpezifischTyp.getById(it) },
            isIndividualAppt,
            runs
        )

        2 -> BeiUnsTermin(
            id.value.toString(),
            schoolyear,
            date,
            contact_school,
            contact_university,
            other,
            school.id.value.toString(),
            school.toDto(),
            internCategory?.let { BeiUnsTyp.getById(it) }
        )
        else -> throw Exception("Terminart nicht erkannt")
    }
}

private val separator = ":"

private fun transformMultiSelect(ids: List<Int>) = ids.fold("") { acc, it -> acc + separator + it }
private fun transformMultiSelect(ids: String): List<Int> {
    val list = mutableListOf<Int?>()
    ids.split(separator).forEach { list.add(it.toIntOrNull()) }
    return list.filterNotNull()
}

@Serializable
data class TerminDto(
    // generelle Eigenschaften
    val uuid: String?,
    val schoolyear: String,
    val date: String,
    val contact_school: String,
    val contact_university: String,
    val other: String,
    val school_id: String,
    val school: SchuleDto? = null,
    val category: Kategorie,
    // spezifische Eigenschaften
    val internCategory: BeiUnsTyp?,
    val schoolCategory: AnSchuleTyp?,
    val kAoACategory: KAoATyp?,
    val talentscoutCategory: TalentscoutTyp?,
    val thSpecificCategory: THSpezifischTyp?,
    val isIndividualAppt: Boolean?,
    val runs: Int?
)