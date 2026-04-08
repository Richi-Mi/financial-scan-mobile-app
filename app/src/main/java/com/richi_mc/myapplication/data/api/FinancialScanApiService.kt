package com.richi_mc.myapplication.data.api

import com.richi_mc.myapplication.data.api.dto.CreateUserRequest
import com.richi_mc.myapplication.data.api.dto.DashboardResponse
import com.richi_mc.myapplication.data.api.dto.ScanRequest
import com.richi_mc.myapplication.data.api.dto.ScanResponse
import com.richi_mc.myapplication.data.api.dto.SyncRequest
import com.richi_mc.myapplication.data.api.dto.SyncResponse
import com.richi_mc.myapplication.data.api.dto.CreateUserResponse
import com.richi_mc.myapplication.data.api.dto.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FinancialScanApiService {

    @POST("api/scan")
    suspend fun scanTicket(
        @Body request: ScanRequest
    ): Response<ScanResponse>

    @POST("api/sync")
    suspend fun syncTicket(
        @Body request: SyncRequest
    ): Response<SyncResponse>

    @GET("api/dashboard")
    suspend fun getDashboard(
        @Query("user_id") userId: String
    ): Response<DashboardResponse>

    @POST("api/users")
    suspend fun createUser(@Body request: CreateUserRequest): Response<CreateUserResponse>

    @GET("api/general")
    suspend fun getGeneral(
        @Query("user_id") userId: String
    ): Response<ProfileResponse>
}