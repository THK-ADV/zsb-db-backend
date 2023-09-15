package model.termin

import model.schule.SchuleDto
import model.termin.enum.AnSchuleTyp
import model.termin.enum.BeiUnsTyp
import model.termin.enum.Kategorie

abstract class AbstrakterTermin(
    val uuid: String?,
    val schoolyear: String,
    val date: String,
    val contact_school: String,
    val contact_university: String,
    val other: String,
    val school_id: String,
    val school: SchuleDto? = null,
)

class AnSchuleTermin(
    uuid: String?,
    schoolyear: String,
    date: String,
    contact_school: String,
    contact_university: String,
    other: String,
    school_id: String,
    school: SchuleDto? = null,
    val schoolCategory: AnSchuleTyp?,
    val isIndividualAppt: Boolean?,
    val runs: Int?,
) : AbstrakterTermin(uuid, schoolyear, date, contact_school, contact_university, other, school_id, school)

class BeiUnsTermin(
    uuid: String?,
    schoolyear: String,
    date: String,
    contact_school: String,
    contact_university: String,
    other: String,
    school_id: String,
    school: SchuleDto? = null,
    val internCategory: BeiUnsTyp?,
) : AbstrakterTermin(uuid, schoolyear, date, contact_school, contact_university, other, school_id, school)

/*
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
