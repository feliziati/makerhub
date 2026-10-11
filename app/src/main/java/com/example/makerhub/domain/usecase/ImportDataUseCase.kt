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
import kotlin.collections.forEach

class ImportDataUseCase(
    private val dataSource: DataSourceRepository,
    private val local: LocalRepository,
    private val isInit: Boolean = true
) {
    companion object {
        private const val TAG = "ImportDataUseCase"
    }

    suspend operator fun invoke(): IResult<String> {
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

                    val log = reportLog.joinToString("\n")
                    Log.i(TAG, log)
                    IResult.Success(log)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Uncaught exception during import: ${e.message}", e)
            IResult.Error(e, "Uncaught exception during import")
        }
    }

    private suspend fun saveItems(items: List<ItemResponse>, report: MutableList<String>): Int {
        var count = 0
        items.forEach { itemResponse ->
            val msg = try {
                itemResponse.validateFields()
                val rowId = local.saveItem(
                    ItemEntity.from(
                        itemResponse.id,
                        itemResponse.name!!,
                        itemResponse.category!!,
                        itemResponse.status!!,
                        itemResponse.updatedAt!!
                    )
                )
                if (rowId != -1L) {
                    count += 1
                    "Successfully saved Item ${itemResponse.id}"
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
                borrowerResponse.validateFields()
                val rowId = local.saveBorrower(
                    BorrowerEntity.from(
                        borrowerResponse.id!!,
                        borrowerResponse.name!!,
                        borrowerResponse.email!!,
                        borrowerResponse.joinedAt!!,
                        borrowerResponse.updatedAt
                    )
                )
                if (rowId != -1L) {
                    count += 1
                    "Successfully saved Borrower ${borrowerResponse.id}"
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
                checkoutResponse.validateFields()
                val rowId = local.saveCheckout(
                    CheckoutEntity.from(
                        checkoutResponse.id!!,
                        checkoutResponse.itemId!!,
                        checkoutResponse.borrowerId!!,
                        checkoutResponse.status!!,
                        checkoutResponse.checkedOutAt!!,
                        checkoutResponse.dueAt!!,
                        checkoutResponse.returnedAt,
                        checkoutResponse.updatedAt!!
                    )
                )
                if (rowId != -1L) {
                    count += 1
                    "Successfully saved Checkout ${checkoutResponse.id}"
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

    // if missing fields is empty, save the item, else report
    private fun ItemResponse.validateFields() {
        val missingFields = mutableListOf<String>()
        this.let {
            if (it.id == null) missingFields.add("id")
            if (it.name.isNullOrEmpty()) missingFields.add("name")
            if (it.category.isNullOrEmpty()) missingFields.add("category")
            if (it.status.isNullOrEmpty()) missingFields.add("status")
            if (it.updatedAt.isNullOrEmpty()) missingFields.add("updated_at")
        }
        if (missingFields.isNotEmpty()) throw Exception("Item $id has missing fields $missingFields")
    }

    private fun BorrowerResponse.validateFields() {
        val missingFields = mutableListOf<String>()
        this.let {
            if (it.id.isNullOrEmpty()) missingFields.add("id")
            if (it.name.isNullOrEmpty()) missingFields.add("name")
            if (it.email.isNullOrEmpty()) missingFields.add("email")
            if (it.joinedAt.isNullOrEmpty()) missingFields.add("joined_at")
        }
        if (missingFields.isNotEmpty()) throw Exception("Borrower $id has missing fields $missingFields")

    }

    private fun CheckoutResponse.validateFields() {
        val missingFields = mutableListOf<String>()
        this.let {
            if (it.id.isNullOrEmpty()) missingFields.add("id")
            if (it.itemId == null) missingFields.add("item_id")
            if (it.borrowerId.isNullOrEmpty()) missingFields.add("borrower_id")
            if (it.status.isNullOrEmpty()) missingFields.add("status")
            if (it.checkedOutAt.isNullOrEmpty()) missingFields.add("checked_out_at")
            if (it.dueAt.isNullOrEmpty()) missingFields.add("returned_at")
            if (it.updatedAt.isNullOrEmpty()) missingFields.add("updated_at")
        }
        if (missingFields.isNotEmpty()) throw Exception("Checkout $id has missing fields $missingFields")
    }
}
