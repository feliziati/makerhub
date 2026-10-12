package com.example.makerhub.domain.usecase

import android.util.Log
import com.example.makerhub.common.IResult
import com.example.makerhub.common.toImpliedItemStatus
import com.example.makerhub.data.remote.response.BorrowerResponse
import com.example.makerhub.data.remote.response.CheckoutResponse
import com.example.makerhub.data.remote.response.ItemResponse
import com.example.makerhub.data.repository.DataSourceRepository
import com.example.makerhub.data.repository.LocalRepository
import com.example.makerhub.domain.model.parseCheckoutStatus
import com.example.makerhub.domain.model.parseItemStatus
import com.example.makerhub.domain.model.toEntityString
import javax.inject.Inject
import kotlin.collections.forEach

class ImportDataUseCase @Inject constructor(
    private val dataSource: DataSourceRepository,
    private val local: LocalRepository,
) {
    companion object {
        private const val TAG = "ImportDataUseCase"
    }

    private var report = ""

    suspend operator fun invoke( isInit: Boolean = true): IResult<String> {
        return try {
            val reportLog = mutableListOf<String>()

            val importedData = if (isInit) {
                dataSource.importInitialData()
            } else {
                dataSource.importDeltaData()
            }

            when (importedData) {
                is IResult.Error -> return IResult.Error(Exception("Something went wrong importing the data"))
                is IResult.Success -> {
                    val data = importedData.data

                    val itemCount = data.items?.let { saveItems(it, reportLog) }
                    val borrowerCount = data.borrowers?.let { saveBorrowers(it, reportLog) }
                    val checkoutCount = data.checkouts?.let { saveCheckout(it, reportLog) }

                    reconcileConflicts(reportLog)

                    reportLog.add("Imported $itemCount new items")
                    reportLog.add("Imported $borrowerCount new borrowers")
                    reportLog.add("Imported $checkoutCount new checkouts")
                    reportLog.add("Import finished at ${System.currentTimeMillis()}")

                    val log = reportLog.joinToString("\n")
                    setReport(log)
                    Log.i(TAG, log)
                    IResult.Success(log)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Uncaught exception during import: ${e.message}", e)
            IResult.Error(e, "Uncaught exception during import")
        }
    }

    fun setReport(report: String) {
        this.report = report
    }

    fun getReport(): String {
        return report
    }

    private suspend fun saveItems(items: List<ItemResponse>, report: MutableList<String>): Int {
        var count = 0
        items.forEach { itemResponse ->
            val msg = try {
                val rowId = local.saveItem(itemResponse)
                if (rowId != -1L) {
                    count += 1
                    "Saved Item ${itemResponse.id}"
                } else {
                    "Skipped Item ${itemResponse.id}, id exists"
                }
            } catch (e: Exception) {
                "Rejected Item ${itemResponse.id}, Reason: ${e.message}"
            }
            report.add(msg)
        }
        return count
    }

    private suspend fun saveBorrowers(borrowers: List<BorrowerResponse>, report: MutableList<String>): Int {
        var count = 0
        borrowers.forEach { borrowerResponse ->
            val msg = try {
                val rowId = local.saveBorrower(borrowerResponse)
                if (rowId != -1L) {
                    count += 1
                    "Saved Borrower ${borrowerResponse.id}"
                } else {
                    "Skipped Borrower ${borrowerResponse.id}, id exists"
                }
            } catch (e: Exception) {
                "Rejected Borrower ${borrowerResponse.id}, Reason: ${e.message}"
            }
            report.add(msg)
        }
        return count
    }

    private suspend fun saveCheckout(checkouts: List<CheckoutResponse>, report: MutableList<String>): Int {
        var count = 0
        checkouts.forEach { checkoutResponse ->
            val msg = try {
                val rowId = local.saveCheckout(checkoutResponse)
                if (rowId != -1L) {
                    count += 1
                    "Saved Checkout ${checkoutResponse.id}"
                } else {
                    "Skipped Checkout ${checkoutResponse.id}, id exists"
                }
            } catch (e: Exception) {
                "Rejected Checkout ${checkoutResponse.id}, Reason: ${e.message}"
            }
            report.add(msg)
        }
        return count
    }

    /*1. When the Checkout event wins (checkout.updatedAt > item.updatedAt):
    ◦The Item record is updated in place: its status field is modified to match the state implied by the checkout event (impliedItemStatus), and its updatedAt timestamp is updated to match the checkout's timestamp.
    ◦ The Checkout event record remains untouched in the database as part of the item's loan history.
    2.  When the Item record wins (item.updatedAt >= checkout.updatedAt):
    ◦The Item record retains its status and timestamp.
    ◦   The Checkout record remains intact in the database as historical data. */
    private suspend fun reconcileConflicts(reportLog: MutableList<String>) {
        val items = local.getAllItemsSync()
        items.forEach { itemEntity ->
            val itemStatus = try {
                parseItemStatus(itemEntity.status)
            } catch (e: Exception) {
                return@forEach
            }

            val checkouts = local.getCheckoutsByItemSync(itemEntity.id)
            if (checkouts.isNotEmpty()) {
                val latestCheckout = checkouts.maxByOrNull { it.updatedAt } ?: return@forEach
                val checkoutStatus = try {
                    parseCheckoutStatus(latestCheckout.status)
                } catch (e: Exception) {
                    return@forEach
                }
                val impliedItemStatus = checkoutStatus.toImpliedItemStatus()

                if (itemStatus != impliedItemStatus) {
                    if (latestCheckout.updatedAt > itemEntity.updatedAt) {
                        val updatedItemEntity = itemEntity.copy(
                            status = impliedItemStatus.toEntityString(),
                            updatedAt = latestCheckout.updatedAt
                        )
                        try {
                            local.saveItem(updatedItemEntity)
                            reportLog.add("Conflict detected for Item ${itemEntity.id}: Item status '$itemStatus' (${itemEntity.updatedAt}) vs Checkout ${latestCheckout.id} status '$checkoutStatus' (${latestCheckout.updatedAt}). Checkout updated_at wins: Item status updated to $impliedItemStatus.")
                        } catch (e: Exception) {
                            reportLog.add("Skipped Item ${itemEntity.id}, Reason: ${e.message}")
                        }
                    } else {
                        reportLog.add("Conflict detected for Item ${itemEntity.id}: Item status '$itemStatus' (${itemEntity.updatedAt}) vs Checkout ${latestCheckout.id} status '$checkoutStatus' (${latestCheckout.updatedAt}). Item updated_at wins: Retained Item status as $itemStatus.")
                    }
                }
            }
        }
    }

}
