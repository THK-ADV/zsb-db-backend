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
    val designation = text("bezeichnung")
    val schoolyear = text("schuljahr")
    val date = text("datum")
    val contact_school = text("kontaktperson schule")
    val contact_university = text("kontaktperson hochschule")
    val other = text("freitextfeld")
    val school_id = reference("schule_id", Schulen)
    val rating = text("bewertung")
    val category = integer("terminart")
    val schoolCategory = text("an schule typ").nullable()
    val kAoACategory = text("kaoa typ").nullable()
    val kAoARuns = integer("kaoa - durchläufe").nullable()
    val kAoAOther = text("kaoa - sonstiges").nullable()
    val talentscoutCategory = text("talentscout typ").nullable()
    val talentscoutOther = text("ts - sonstiges").nullable()
    val thSpecificCategory = text("th spezifisch typ").nullable()
    val thRunsSingle = integer("th spezifisch - durchläufe einzeltermin").nullable()
    val thOtherSingle = text("th spezifisch - sonstiges einzeltermin").nullable()
    val thRunsFair = integer("th spezifisch - durchläufe schulmesse").nullable()
    val thOtherFair = text("th spezifisch - sonstiges schulmesse").nullable()
    val internCategory = text("bei uns typ").nullable()
    val internOther = text("bei uns - sonstiges").nullable()
}

class Termin(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    private var designation by Termine.designation
    private var schoolyear by Termine.schoolyear
    private var date by Termine.date
    private var contact_school by Termine.contact_school
    private var contact_university by Termine.contact_university
    private var other by Termine.other
    private var school by Schule referencedOn Termine.school_id
    private var rating by Termine.rating
    private var category by Termine.category
    private var schoolCategory by Termine.schoolCategory
    private var kAoACategory by Termine.kAoACategory
    private var kAoARuns by Termine.kAoARuns
    private var kAoAOther by Termine.kAoAOther
    private var talentscoutCategory by Termine.talentscoutCategory
    private var talentscoutOther by Termine.talentscoutOther
    private var thSpecificCategory by Termine.thSpecificCategory
    private var thRunsSingle by Termine.thRunsSingle
    private var thOtherSingle by Termine.thOtherSingle
    private var thRunsFair by Termine.thRunsFair
    private var thOtherFair by Termine.thOtherFair
    private var internCategory by Termine.internCategory
    private var internOther by Termine.internOther

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

    private fun update(dto: TerminDto, school: Schule) {
        this.designation = dto.designation
        this.schoolyear = dto.schoolyear
        this.date = dto.date
        this.contact_school = dto.contact_school
        this.contact_university = dto.contact_university
        this.other = dto.other
        this.school = school
        this.rating = dto.rating
        this.category = dto.category.id
        this.schoolCategory = dto.schoolCategory.toString()
        this.kAoACategory = dto.kAoACategory.toString()
        this.kAoARuns = dto.kAoARuns
        this.kAoAOther = dto.kAoAOther
        this.talentscoutCategory = dto.talentscoutCategory.toString()
        this.talentscoutOther = dto.talentscoutOther
        this.thSpecificCategory = dto.thSpecificCategory.toString()
        this.thRunsSingle = dto.thRunsSingle
        this.thOtherSingle = dto.thOtherSingle
        this.thRunsFair = dto.thRunsFair
        this.thOtherFair = dto.thOtherFair
        this.internCategory = dto.internCategory.toString()
        this.internOther = dto.internOther
    }

    fun toDto() = TerminDto(
        id.value.toString(),
        designation,
        schoolyear,
        date,
        contact_school,
        contact_university,
        other,
        school.id.value.toString(),
        null,
        rating,
        Kategorie.getById(category),
        schoolCategory?.let {
            it.drop(1).dropLast(1).split(", ").map { id -> AnSchuleTyp.getById(id.toInt()) }
        },
        kAoACategory?.let {
            it.drop(1).dropLast(1).split(", ").map { id -> KAoATyp.getById(id.toInt()) }
        },
        kAoARuns,
        kAoAOther,
        talentscoutCategory?.let {
            it.drop(1).dropLast(1).split(", ").map { id -> TalentscoutTyp.getById(id.toInt()) }
        },
        talentscoutOther,
        thSpecificCategory?.let {
            it.drop(1).dropLast(1).split(", ").map { id -> THSpezifischTyp.getById(id.toInt()) }
        },
        thRunsSingle,
        thOtherSingle,
        thRunsFair,
        thOtherFair,
        internCategory?.let {
            it.drop(1).dropLast(1).split(", ").map { id -> BeiUnsTyp.getById(id.toInt()) }
        },
        internOther,
    )

    /*fun toAtomicDto() = TerminDto(
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
        thSpecificCategory?.let { THSpezifischTyp.getById(it) }
    )*/

    fun toTermin() = when (category) {
        1 -> AnSchuleTermin(
            id.value.toString(),
            designation,
            schoolyear,
            date,
            contact_school,
            contact_university,
            other,
            school.id.value.toString(),
            school.toDto(),
            rating,
            schoolCategory?.let {
                it.split(",").map { it.trim() }
            },
            kAoACategory?.let {
                it.split(",").map { it.trim() }
            },
            kAoARuns,
            kAoAOther,
            talentscoutCategory?.let {
                it.split(",").map { it.trim() }
            },
            talentscoutOther,
            thSpecificCategory?.let {
                it.split(",").map { it.trim() }
            },
            thRunsSingle,
            thOtherSingle,
            thRunsFair,
            thOtherFair
        )

        2 -> BeiUnsTermin(
            id.value.toString(),
            designation,
            schoolyear,
            date,
            contact_school,
            contact_university,
            other,
            school.id.value.toString(),
            school.toDto(),
            rating,
            internCategory?.let {
                it.split(",").map { it.trim() }
            },
            internOther
        )

        3 -> BeiDrittenTermin(
            id.value.toString(),
            designation,
            schoolyear,
            date,
            contact_school,
            contact_university,
            other,
            school.id.value.toString(),
            school.toDto(),
            rating
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
    val designation: String,
    val schoolyear: String,
    val date: String,
    val contact_school: String,
    val contact_university: String,
    val other: String,
    val school_id: String,
    val school: SchuleDto? = null,
    val rating: String,
    val category: Kategorie,
    // spezifische Eigenschaften
    val schoolCategory: List<AnSchuleTyp>?,
    val kAoACategory: List<KAoATyp>?,
    val kAoARuns: Int?,
    val kAoAOther: String?,
    val talentscoutCategory: List<TalentscoutTyp>?,
    val talentscoutOther: String?,
    val thSpecificCategory: List<THSpezifischTyp>?,
    val thRunsSingle: Int?,
    val thOtherSingle: String?,
    val thRunsFair: Int?,
    val thOtherFair: String?,
    val internCategory: List<BeiUnsTyp>?,
    val internOther: String?,
)