package model.termin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.schule.SchuleDto
import model.termin.enum.*

@Serializable
sealed class AbstrakterTermin {
    abstract val uuid: String?
    abstract val schoolyear: String
    abstract val date: String
    abstract val contact_school: String
    abstract val contact_university: String
    abstract val other: String
    abstract val school_id: String
    abstract val school: SchuleDto?
}

@Serializable
@SerialName("AnSchuleTermin")
class AnSchuleTermin (
    override val uuid: String?,
    override val schoolyear: String,
    override val date: String,
    override val contact_school: String,
    override val contact_university: String,
    override val other: String,
    override val school_id: String,
    override val school: SchuleDto? = null,
    val schoolCategory: AnSchuleTyp?,
    val kAoACategory: KAoATyp?,
    val talentscoutCategory: TalentscoutTyp?,
    val thSpecificCategory: THSpezifischTyp?,
    val isIndividualAppt: Boolean?,
    val runs: Int?
): AbstrakterTermin()

@Serializable
@SerialName("BeiUnsTermin")
class BeiUnsTermin(
    override val uuid: String?,
    override val schoolyear: String,
    override val date: String,
    override val contact_school: String,
    override val contact_university: String,
    override val other: String,
    override val school_id: String,
    override val school: SchuleDto? = null,
    val internCategory: BeiUnsTyp?,
) : AbstrakterTermin()

/*
@Serializable
@SerialName("BeiDrittenTermin")
class BeiDrittenTermin(
    uuid: String?,
    schoolyear: String,
    date: String,
    contact_school: String,
    contact_university: String,
    other: String,
    school_id: String,
    school: SchuleDto? = null,
    category: Kategorie,
) : AbstrakterTermin(uuid, schoolyear, date, contact_school, contact_university, other, school_id, school, category)*/
