package model.kaoaarbeit

import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object KAoAArbeitDao {
    fun bySchoolId(schoolId: UUID, atomic: Boolean): List<KAoAArbeitDto> = transaction {
        KAoAArbeit
            .find { KAoAArbeiten.school eq schoolId }
            .map { if (atomic) it.toAtomicDto() else it.toDto() }
    }
}