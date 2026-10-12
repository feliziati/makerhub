package com.example.makerhub.data.repository.impl

import android.content.Context
import android.util.Log
import com.example.makerhub.common.IResult
import com.example.makerhub.data.remote.adapter.StringToIntAdapter
import com.example.makerhub.data.remote.response.LedgerDataResponse
import com.example.makerhub.data.repository.DataSourceRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.InputStream
import java.io.InputStreamReader

class DataSourceRepositoryImpl(
    private val context: Context,
    private val gson: Gson = createGson()
) : DataSourceRepository {

    private val TAG = DataSourceRepositoryImpl::class.java.simpleName

    companion object {
        const val FILENAME_INITIAL_LEDGER = "ledger_initial.json"
        const val FILENAME_DELTA_LEDGER = "ledger_delta.json"

        fun createGson(): Gson {
            return GsonBuilder()
                .create()
        }
    }

    override suspend fun importInitialData(): IResult<LedgerDataResponse> {
        return try {
            val data = importLedgerResponse(FILENAME_INITIAL_LEDGER)
            IResult.Success(data)
        } catch (e: Exception) {
            IResult.Error(e)
        }
    }

    override suspend fun importDeltaData(): IResult<LedgerDataResponse> {
        return try {
            val data = importLedgerResponse(FILENAME_DELTA_LEDGER)
            IResult.Success(data)
        } catch (e: Exception) {
            IResult.Error(e)
        }
    }

    /**
     * Imports a JSON sample file from the /source/sample folder (or assets) and parses it using Gson.
     *
     * @param fileName The JSON filename (e.g., "ledger_initial.json" or "ledger_delta.json")
     * @return [LedgerDataResponse] or null if the file could not be read or parsed
     */
    private fun importLedgerResponse(fileName: String): LedgerDataResponse {
        return try {
            parseLedgerStream(getInputStream(fileName))
        } catch (e: Exception) {
            throw e
        }
    }

    fun parseLedgerJson(jsonString: String): LedgerDataResponse {
        return try {
            gson.fromJson(jsonString, LedgerDataResponse::class.java)
        } catch (e: Exception) {
            throw e
        }
    }

    fun parseLedgerStream(inputStream: InputStream): LedgerDataResponse {
        return try {
            inputStream.use { stream ->
                InputStreamReader(stream).use { reader ->
                    gson.fromJson(reader, LedgerDataResponse::class.java)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun getInputStream(fileName: String): InputStream {
        try {
            return context.assets.open(fileName)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening $fileName: ${e.printStackTrace()}")
            throw e
        }
    }
}