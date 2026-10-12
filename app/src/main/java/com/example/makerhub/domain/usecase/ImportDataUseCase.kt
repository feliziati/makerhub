package com.example.makerhub.domain.usecase

import android.util.Log
import com.example.makerhub.common.IResult
import com.example.makerhub.data.local.entity.BorrowerEntity
import com.example.makerhub.data.local.entity.CheckoutEntity
import com.example.makerhub.data.local.entity.ItemEntity
import com.example.makerhub.data.remote.response.BorrowerResponse
import com.example.makerhub.data.remote.response.CheckoutResponse
import com.example.makerhub.data.remote.response.ItemResponse
import com.example.makerhub.data.repository.DataSourceRepository
import com.example.makerhub.data.repository.LocalRepository
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

}
