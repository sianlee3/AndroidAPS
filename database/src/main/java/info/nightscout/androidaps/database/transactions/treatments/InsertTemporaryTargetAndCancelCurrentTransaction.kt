package info.nightscout.androidaps.database.transactions.treatments

import info.nightscout.androidaps.database.entities.TemporaryTarget
import info.nightscout.androidaps.database.transactions.Transaction
import java.util.*

/**
 * Inserts a TemporaryTarget into the database and cancels the active at the specified timestamp
 *      if there is one by adjusting the duration property
 */
class InsertTemporaryTargetAndCancelCurrentTransaction(
        val timestamp: Long,
        val duration: Long,
        val reason: TemporaryTarget.Reason,
        val target: Double
) : Transaction<Unit>() {
    override fun run() {
        val currentlyActive = database.temporaryTargetDao.getTemporaryTargetActiveAt(timestamp)
        if (currentlyActive != null) {
            currentlyActive.duration = timestamp - currentlyActive.timestamp
            database.temporaryTargetDao.updateExistingEntry(currentlyActive)
        }
        database.temporaryTargetDao.insertNewEntry(TemporaryTarget(
                timestamp = timestamp,
                utcOffset = TimeZone.getDefault().getOffset(timestamp).toLong(),
                reason = reason,
                target = target,
                duration = duration
        ))
    }
}