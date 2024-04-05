package model.termin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.schule.SchuleDto
import model.termin.enum.*
import model.termin.kontakte.KontaktHochschuleDto
import model.termin.kontakte.KontaktSchuleDto

@Serializable
sealed class AbstrakterTermin {
    abstract val uuid: String?
    abstract val designation: String
    abstract val schoolyear: String
    abstract val date: String
    abstract val contact_school_id: String
    abstract val contact_school: KontaktSchuleDto?
    abstract val contact_university_id: String
    abstract val contact_university: KontaktHochschuleDto?
    abstract val other: String
    abstract val school_id: String
    abstract val school: SchuleDto?
    abstract val rating: String
}

@Serializable
@SerialName("AnSchuleTermin")
class AnSchuleTermin (
    override val uuid: String?,
    override val designation: String,
    override val schoolyear: String,
    override val date: String,
    override val contact_school_id: String,
    override val contact_school: KontaktSchuleDto?,
    override val contact_university_id: String,
    override val contact_university: KontaktHochschuleDto?,
    override val other: String,
    override val school_id: String,
    override val school: SchuleDto? = null,
    override val rating: String,
    val schoolCategory: List<String>?,
    val kAoACategory: List<String>?,
    val kAoARuns: Int?,
    val kAoAOther: String?,
    val talentscoutCategory: List<String>?,
    val talentscoutOther: String?,
    val thSpecificCategory: List<String>?,
    val thRunsSingle: Int?,
    val thOtherSingle: String?,
    val thRunsFair: Int?,
    val thOtherFair: String?
): AbstrakterTermin()

@Serializable
@SerialName("BeiUnsTermin")
class BeiUnsTermin(
    override val uuid: String?,
    override val designation: String,
    override val schoolyear: String,
    override val date: String,
    override val contact_school_id: String,
    override val contact_school: KontaktSchuleDto?,
    override val contact_university_id: String,
    override val contact_university: KontaktHochschuleDto?,
    override val other: String,
    override val school_id: String,
    override val school: SchuleDto? = null,
    override val rating: String,
    val internCategory: List<String>?,
    val internOther: String?
) : AbstrakterTermin()

@Serializable
@SerialName("BeiDrittenTermin")
class BeiDrittenTermin(
    override val uuid: String?,
    override val designation: String,
    override val schoolyear: String,
    override val date: String,
    override val contact_school_id: String,
    override val contact_school: KontaktSchuleDto?,
    override val contact_university_id: String,
    override val contact_university: KontaktHochschuleDto?,
    override val other: String,
    override val school_id: String,
    override val school: SchuleDto? = null,
    override val rating: String
) : AbstrakterTermin()
