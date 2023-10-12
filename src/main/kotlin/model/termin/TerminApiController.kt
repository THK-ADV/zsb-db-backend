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
    var internCategory: BeiUnsTyp? = null
    var schoolCategory: AnSchuleTyp? = null
    var kAoACategory: KAoATyp? = null
    var talentscoutCategory: TalentscoutTyp? = null
    var thSpecificCategory: THSpezifischTyp? = null
    var isIndividualAppt: Boolean? = null
    var runs: Int? = null
    var description: String? = null
    if (abstrakterTermin is AnSchuleTermin) {
        category = Kategorie.SCHOOL
        schoolCategory = abstrakterTermin.schoolCategory
        kAoACategory = abstrakterTermin.kAoACategory
        talentscoutCategory = abstrakterTermin.talentscoutCategory
        thSpecificCategory = abstrakterTermin.thSpecificCategory
        isIndividualAppt = abstrakterTermin.isIndividualAppt
        runs = abstrakterTermin.runs
    } else if (abstrakterTermin is BeiUnsTermin) {
        category = Kategorie.INTERN
        internCategory = abstrakterTermin.internCategory
    } else if (abstrakterTermin is BeiDrittenTermin) {
        description = abstrakterTermin.description
    }
    return TerminDto(
        uuid = abstrakterTermin.uuid,
        schoolyear = abstrakterTermin.schoolyear,
        date = abstrakterTermin.date,
        contact_school = abstrakterTermin.contact_school,
        contact_university = abstrakterTermin.contact_university,
        other = abstrakterTermin.other,
        school_id = abstrakterTermin.school_id,
        school = abstrakterTermin.school,
        category = category,
        internCategory = internCategory,
        schoolCategory = schoolCategory,
        kAoACategory = kAoACategory,
        talentscoutCategory = talentscoutCategory,
        thSpecificCategory = thSpecificCategory,
        isIndividualAppt = isIndividualAppt,
        runs = runs,
        description = description
    )
}

private suspend fun postOrPut(call: ApplicationCall, isPost: Boolean = false) {
    call.logRequest()
    val abstrakterTermin = call.receive<AbstrakterTermin>()
    val terminDto = toTerminDto(abstrakterTermin)
    if (isPost) {
        if (call.checkId(abstrakterTermin.uuid)) return
    } else
        if (call.checkIdAndRespondUsePostIfNull(abstrakterTermin.uuid)) return

    val result = TerminDao.createOrUpdate(terminDto)
    call.respond(HttpServerResponse.map(result))
}
