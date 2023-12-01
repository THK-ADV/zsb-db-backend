package model.termin

import error_handling.HttpServerResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import model.termin.enum.*
import utilty.*

fun Route.termineApi() {
    route("events") {

        // get Kategorie options
        get("/categories") {
            call.logRequest()
            val json = Json.encodeToJsonElement(KategorieDto.generate())
            call.respondJsonOk(json)
        }

        // get stufe options
        get("/levels") {
            call.logRequest()
            val json = Json.encodeToJsonElement(StufeDto.generate())
            call.respondJsonOk(json)
        }

        // get vortragsart options
        get("/presentationtypes") {
            call.logRequest()
            val json = Json.encodeToJsonElement(VortragsartDto.generate())
            call.respondJsonOk(json)
        }

        // get all
        get {
            call.logRequest()
            val result = TerminDao.getAll()
            call.respond(HttpServerResponse.map(result))
        }

        // get one by id
        get("/{uuid}") {
            call.logRequest()
            val uuid = call.parseParamAsUUID("uuid") ?: return@get
            val result = TerminDao.getById(uuid)
            call.respond(HttpServerResponse.map(result))
        }

        // create
        post { postOrPut(call, isPost = true) }

        // update
        put { postOrPut(call) }

        // delete
        delete("/{uuid}") {
            call.logRequest()
            val uuid = call.parseParamAsUUID("uuid") ?: return@delete
            val isDeleted = TerminDao.delete(uuid)
            if (isDeleted)
                call.respondTextAsJson("Successfully deleted $uuid")
            else
                call.respondTextAsJson("Couldn't find Termin with id: $uuid", status = HttpStatusCode.NotFound)
        }
    }
}

private fun toTerminDto(abstrakterTermin: AbstrakterTermin): TerminDto {
    var category = Kategorie.UNKNOWN
    var schoolCategory: List<AnSchuleTyp>? = null
    var kAoACategory: List<KAoATyp>? = null
    var kAoARuns: Int? = null
    var kAoAOther: String? = null
    var talentscoutCategory: List<TalentscoutTyp>? = null
    var talentscoutOther: String? = null
    var thSpecificCategory: List<THSpezifischTyp>? = null
    var thRunsSingle: Int? = null
    var thOtherSingle: String? = null
    var thRunsFair: Int? = null
    var thOtherFair: String? = null
    var internCategory: List<BeiUnsTyp>? = null
    var internOther: String? = null

    when (abstrakterTermin) {
        is AnSchuleTermin -> {
            category = Kategorie.SCHOOL
            schoolCategory = abstrakterTermin.schoolCategory?.map { AnSchuleTyp.getByDesc(it) }
            kAoACategory = abstrakterTermin.kAoACategory?.map { KAoATyp.getByDesc(it) }
            kAoARuns = abstrakterTermin.kAoARuns
            kAoAOther = abstrakterTermin.kAoAOther
            talentscoutCategory = abstrakterTermin.talentscoutCategory?.map { TalentscoutTyp.getByDesc(it) }
            talentscoutOther = abstrakterTermin.talentscoutOther
            thSpecificCategory = abstrakterTermin.thSpecificCategory?.map { THSpezifischTyp.getByDesc(it) }
            thRunsSingle = abstrakterTermin.thRunsSingle
            thOtherSingle = abstrakterTermin.thOtherSingle
            thRunsFair = abstrakterTermin.thRunsFair
            thOtherFair = abstrakterTermin.thOtherFair
        }
        is BeiUnsTermin -> {
            category = Kategorie.INTERN
            internCategory = abstrakterTermin.internCategory?.map { BeiUnsTyp.getByDesc(it) }
            internOther = abstrakterTermin.internOther
        }
        is BeiDrittenTermin -> category = Kategorie.THIRD
    }
    return TerminDto(
        uuid = abstrakterTermin.uuid,
        designation = abstrakterTermin.designation,
        schoolyear = abstrakterTermin.schoolyear,
        date = abstrakterTermin.date,
        contact_school = abstrakterTermin.contact_school,
        contact_university = abstrakterTermin.contact_university,
        other = abstrakterTermin.other,
        school_id = abstrakterTermin.school_id,
        school = abstrakterTermin.school,
        rating = abstrakterTermin.rating,
        category = category,
        schoolCategory = schoolCategory,
        kAoACategory = kAoACategory,
        kAoARuns = kAoARuns,
        kAoAOther = kAoAOther,
        talentscoutCategory = talentscoutCategory,
        talentscoutOther = talentscoutOther,
        thSpecificCategory = thSpecificCategory,
        thRunsSingle = thRunsSingle,
        thOtherSingle = thOtherSingle,
        thRunsFair = thRunsFair,
        thOtherFair = thOtherFair,
        internCategory = internCategory,
        internOther = internOther,
    )
}

private suspend fun postOrPut(call: ApplicationCall, isPost: Boolean = false) {
    call.logRequest()
    val abstrakterTermin = call.receive<AbstrakterTermin>()
    val terminDto = toTerminDto(abstrakterTermin)
    if (isPost) {
        if (call.checkId(abstrakterTermin.uuid)) return
    } else {
        if (call.checkIdAndRespondUsePostIfNull(abstrakterTermin.uuid)) return
    }

    val result = TerminDao.createOrUpdate(terminDto)
    call.respond(HttpServerResponse.map(result))
}
