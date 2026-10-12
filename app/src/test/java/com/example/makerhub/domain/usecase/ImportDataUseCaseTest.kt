package com.example.makerhub.domain.usecase

import android.util.Log
import com.example.makerhub.common.IResult
import com.example.makerhub.data.remote.response.BorrowerResponse
import com.example.makerhub.data.remote.response.CheckoutResponse
import com.example.makerhub.data.remote.response.ItemResponse
import com.example.makerhub.data.remote.response.LedgerDataResponse
import com.example.makerhub.data.repository.DataSourceRepository
import com.example.makerhub.data.repository.LocalRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ImportDataUseCaseTest {

    private lateinit var dataSourceRepository: DataSourceRepository
    private lateinit var localRepository: LocalRepository
    private lateinit var importDataUseCase: ImportDataUseCase

    @Before
    fun setUp() {
        dataSourceRepository = mockk()
        localRepository = mockk(relaxed = true)

        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `invoke initial data success saves items borrowers checkouts and returns success`() = runTest {
        val ledgerData = LedgerDataResponse(
            items = listOf(
                ItemResponse(id = 1, name = "Drill", category = "tool", status = "available", updatedAt = "2026-01-01T00:00:00Z")
            ),
            borrowers = listOf(
                BorrowerResponse(id = "b1", name = "Alice", email = "alice@example.com", joinedAt = "2026-01-01T00:00:00Z")
            ),
            checkouts = listOf(
                CheckoutResponse(id = "c1", itemId = 1, borrowerId = "b1", status = "active", checkedOutAt = "2026-01-01T00:00:00Z", dueAt = "2026-01-08T00:00:00Z", updatedAt = "2026-01-01T00:00:00Z")
            )
        )
        coEvery { dataSourceRepository.importInitialData() } returns IResult.Success(ledgerData)
        coEvery { localRepository.saveItem(any<ItemResponse>()) } returns 1L
        coEvery { localRepository.saveBorrower(any<BorrowerResponse>()) } returns 1L
        coEvery { localRepository.saveCheckout(any<CheckoutResponse>()) } returns 1L

        importDataUseCase = ImportDataUseCase(dataSourceRepository, localRepository)
        val result = importDataUseCase(isInit = true)

        assertTrue(result is IResult.Success)
        coVerify(exactly = 1) { dataSourceRepository.importInitialData() }
        coVerify(exactly = 0) { dataSourceRepository.importDeltaData() }
        coVerify(exactly = 1) { localRepository.saveItem(any<ItemResponse>()) }
        coVerify(exactly = 1) { localRepository.saveBorrower(any<BorrowerResponse>()) }
        coVerify(exactly = 1) { localRepository.saveCheckout(any<CheckoutResponse>()) }
    }

    @Test
    fun `invoke delta data success calls importDeltaData`() = runTest {
        val ledgerData = LedgerDataResponse(
            items = emptyList(),
            borrowers = emptyList(),
            checkouts = emptyList()
        )
        coEvery { dataSourceRepository.importDeltaData() } returns IResult.Success(ledgerData)

        importDataUseCase = ImportDataUseCase(dataSourceRepository, localRepository)
        val result = importDataUseCase(isInit = false)

        assertTrue(result is IResult.Success)
        coVerify(exactly = 0) { dataSourceRepository.importInitialData() }
        coVerify(exactly = 1) { dataSourceRepository.importDeltaData() }
    }

    @Test
    fun `invoke when dataSource throws exception returns Error`() = runTest {
        coEvery { dataSourceRepository.importInitialData() } throws RuntimeException("Unexpected crash")

        importDataUseCase = ImportDataUseCase(dataSourceRepository, localRepository)
        val result = importDataUseCase(isInit = true)

        assertTrue(result is IResult.Error)
        assertEquals("Unexpected crash", (result as IResult.Error).thr.message)
    }

    @Test
    fun `invoke with invalid item and skipped item handles exceptions and saves valid`() = runTest {
        val ledgerData = LedgerDataResponse(
            items = listOf(
                ItemResponse(id = null, name = "Invalid Item", category = "tool", status = "available", updatedAt = "2026-01-01T00:00:00Z"), // missing id
                ItemResponse(id = 1, name = "Valid Item", category = "tool", status = "available", updatedAt = "2026-01-01T00:00:00Z"),
                ItemResponse(id = 2, name = "Existing Item", category = "tool", status = "available", updatedAt = "2026-01-01T00:00:00Z")
            ),
            borrowers = emptyList(),
            checkouts = emptyList()
        )
        coEvery { dataSourceRepository.importInitialData() } returns IResult.Success(ledgerData)
        coEvery { localRepository.saveItem(match<ItemResponse> { it.id == 1 }) } returns 1L
        coEvery { localRepository.saveItem(match<ItemResponse> { it.id == 2 }) } returns -1L // skipped

        importDataUseCase = ImportDataUseCase(dataSourceRepository, localRepository)
        val result = importDataUseCase(isInit = true)

        assertTrue(result is IResult.Success)
        coVerify(exactly = 1) { localRepository.saveItem(match<ItemResponse> { it.id == 1 }) }
        coVerify(exactly = 1) { localRepository.saveItem(match<ItemResponse> { it.id == 2 }) }
    }

    @Test
    fun `import initial data then delta data twice results in identical item count checkout count and item status`() = runTest {
        val initialLedger = LedgerDataResponse(
            items = listOf(
                ItemResponse(id = 1, name = "Item 1", category = "tool", status = "available", updatedAt = "2026-01-01T00:00:00Z")
            ),
            borrowers = listOf(
                BorrowerResponse(id = "b1", name = "Alice", email = "alice@example.com", joinedAt = "2026-01-01T00:00:00Z")
            ),
            checkouts = listOf(
                CheckoutResponse(id = "c1", itemId = 1, borrowerId = "b1", status = "active", checkedOutAt = "2026-01-01T00:00:00Z", dueAt = "2026-01-08T00:00:00Z", updatedAt = "2026-01-01T00:00:00Z")
            )
        )

        val deltaLedger = LedgerDataResponse(
            items = listOf(
                ItemResponse(id = 1, name = "Item 1 Updated", category = "tool", status = "on_loan", updatedAt = "2026-01-02T00:00:00Z"),
                ItemResponse(id = 2, name = "Item 2", category = "tool", status = "available", updatedAt = "2026-01-02T00:00:00Z")
            ),
            borrowers = emptyList(),
            checkouts = listOf(
                CheckoutResponse(id = "c2", itemId = 2, borrowerId = "b1", status = "active", checkedOutAt = "2026-01-02T00:00:00Z", dueAt = "2026-01-09T00:00:00Z", updatedAt = "2026-01-02T00:00:00Z")
            )
        )

        coEvery { dataSourceRepository.importInitialData() } returns IResult.Success(initialLedger)
        coEvery { dataSourceRepository.importDeltaData() } returns IResult.Success(deltaLedger)

        val itemsMap = mutableMapOf<Int, String>()
        val checkoutsMap = mutableMapOf<String, String>()
        val borrowersMap = mutableMapOf<String, String>()

        coEvery { localRepository.saveItem(any<ItemResponse>()) } answers {
            val item = firstArg<ItemResponse>()
            item.id?.let { itemsMap[it] = item.status.orEmpty() }
            1L
        }
        coEvery { localRepository.saveBorrower(any<BorrowerResponse>()) } answers {
            val b = firstArg<BorrowerResponse>()
            b.id?.let { borrowersMap[it] = b.name.orEmpty() }
            1L
        }
        coEvery { localRepository.saveCheckout(any<CheckoutResponse>()) } answers {
            val c = firstArg<CheckoutResponse>()
            c.id?.let { checkoutsMap[it] = c.status.orEmpty() }
            1L
        }

        // 1. Import initial data
        val initialUseCase = ImportDataUseCase(dataSourceRepository, localRepository)
        val initialResult = initialUseCase(isInit = true)
        assertTrue(initialResult is IResult.Success)

        // 2. Import delta data (first time)
        val deltaUseCase1 = ImportDataUseCase(dataSourceRepository, localRepository)
        val deltaResult1 = deltaUseCase1(isInit = false)
        assertTrue(deltaResult1 is IResult.Success)

        val itemCountAfterDelta1 = itemsMap.size
        val checkoutCountAfterDelta1 = checkoutsMap.size
        val itemStatusesAfterDelta1 = itemsMap.toMap()

        // 3. Import delta data (second time)
        val deltaUseCase2 = ImportDataUseCase(dataSourceRepository, localRepository)
        val deltaResult2 = deltaUseCase2(isInit = false)
        assertTrue(deltaResult2 is IResult.Success)

        val itemCountAfterDelta2 = itemsMap.size
        val checkoutCountAfterDelta2 = checkoutsMap.size
        val itemStatusesAfterDelta2 = itemsMap.toMap()

        // 4. Assert true that Item count, checkout count, and item status is identical after the second delta import
        assertEquals(itemCountAfterDelta1, itemCountAfterDelta2)
        assertEquals(checkoutCountAfterDelta1, checkoutCountAfterDelta2)
        assertEquals(itemStatusesAfterDelta1, itemStatusesAfterDelta2)
    }

    @Test
    fun `invoke with brand new item having invalid item status rejects item and reports reason`() = runTest {
        val ledgerData = LedgerDataResponse(
            items = listOf(
                ItemResponse(id = 10, name = "Broken Saw", category = "tool", status = "invalid_status", updatedAt = "2026-01-01T00:00:00Z")
            ),
            borrowers = emptyList(),
            checkouts = emptyList()
        )
        coEvery { dataSourceRepository.importInitialData() } returns IResult.Success(ledgerData)
        coEvery { localRepository.saveItem(match<ItemResponse> { it.status == "invalid_status" }) } throws IllegalArgumentException("Invalid status provided: invalid_status")

        importDataUseCase = ImportDataUseCase(dataSourceRepository, localRepository)
        val result = importDataUseCase(isInit = true)

        assertTrue(result is IResult.Success)
        val report = (result as IResult.Success).data
        assertTrue(report.contains("Invalid status provided: invalid_status"))
    }

    @Test
    fun `invoke with brand new checkout having invalid checkout status rejects checkout and reports reason`() = runTest {
        val ledgerData = LedgerDataResponse(
            items = emptyList(),
            borrowers = emptyList(),
            checkouts = listOf(
                CheckoutResponse(id = "c10", itemId = 1, borrowerId = "b1", status = "cancelled", checkedOutAt = "2026-01-01T00:00:00Z", dueAt = "2026-01-08T00:00:00Z", updatedAt = "2026-01-01T00:00:00Z")
            )
        )
        coEvery { dataSourceRepository.importInitialData() } returns IResult.Success(ledgerData)
        coEvery { localRepository.saveCheckout(match<CheckoutResponse> { it.status == "cancelled" }) } throws IllegalArgumentException("invalid status provided: cancelled")

        importDataUseCase = ImportDataUseCase(dataSourceRepository, localRepository)
        val result = importDataUseCase(isInit = true)

        assertTrue(result is IResult.Success)
        val report = (result as IResult.Success).data
        assertTrue(report.contains("invalid status provided: cancelled"))
    }

    @Test
    fun `invoke with brand new item having valid item statuses saves successfully`() = runTest {
        val validStatuses = listOf("available", "on_loan", "lost", "retired")
        val ledgerData = LedgerDataResponse(
            items = validStatuses.mapIndexed { index, status ->
                ItemResponse(id = index + 1, name = "Item $index", category = "tool", status = status, updatedAt = "2026-01-01T00:00:00Z")
            },
            borrowers = emptyList(),
            checkouts = emptyList()
        )
        coEvery { dataSourceRepository.importInitialData() } returns IResult.Success(ledgerData)
        coEvery { localRepository.saveItem(any<ItemResponse>()) } returns 1L

        importDataUseCase = ImportDataUseCase(dataSourceRepository, localRepository)
        val result = importDataUseCase(isInit = true)

        assertTrue(result is IResult.Success)
        coVerify(exactly = 4) { localRepository.saveItem(any<ItemResponse>()) }
    }

    @Test
    fun `invoke with brand new checkout having valid checkout statuses saves successfully`() = runTest {
        val validStatuses = listOf("active", "returned", "lost")
        val ledgerData = LedgerDataResponse(
            items = emptyList(),
            borrowers = emptyList(),
            checkouts = validStatuses.mapIndexed { index, status ->
                CheckoutResponse(id = "c$index", itemId = 1, borrowerId = "b1", status = status, checkedOutAt = "2026-01-01T00:00:00Z", dueAt = "2026-01-08T00:00:00Z", updatedAt = "2026-01-01T00:00:00Z")
            }
        )
        coEvery { dataSourceRepository.importInitialData() } returns IResult.Success(ledgerData)
        coEvery { localRepository.saveCheckout(any<CheckoutResponse>()) } returns 1L

        importDataUseCase = ImportDataUseCase(dataSourceRepository, localRepository)
        val result = importDataUseCase(isInit = true)

        assertTrue(result is IResult.Success)
        coVerify(exactly = 3) { localRepository.saveCheckout(any<CheckoutResponse>()) }
    }

    @Test
    fun `invoke when saving item throws exception reports rejection reason`() = runTest {
        val ledgerData = LedgerDataResponse(
            items = listOf(
                ItemResponse(id = 1, name = "Drill", category = "tool", status = "available", updatedAt = "2026-01-01T00:00:00Z")
            ),
            borrowers = emptyList(),
            checkouts = emptyList()
        )
        coEvery { dataSourceRepository.importInitialData() } returns IResult.Success(ledgerData)
        val errorMessage = "Cannot transition Item 1 from LOST to AVAILABLE"
        coEvery { localRepository.saveItem(any<ItemResponse>()) } throws IllegalArgumentException(errorMessage)

        importDataUseCase = ImportDataUseCase(dataSourceRepository, localRepository)
        val result = importDataUseCase(isInit = true)

        assertTrue(result is IResult.Success)
        val report = (result as IResult.Success).data
        assertTrue(report.contains(errorMessage))
    }
}
