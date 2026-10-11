package com.example.makerhub.data.repository

import com.example.makerhub.common.IResult
import com.example.makerhub.data.remote.response.LedgerDataResponse


interface DataSourceRepository {
    suspend fun importInitialData(): IResult<LedgerDataResponse>
    suspend fun importDeltaData(): IResult<LedgerDataResponse>
}
