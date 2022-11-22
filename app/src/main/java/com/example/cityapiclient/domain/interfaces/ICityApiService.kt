package com.example.cityapiclient.domain.interfaces

import com.example.cityapiclient.data.ServiceResult
import com.example.cityapiclient.data.remote.models.CityApiResponse
import com.example.cityapiclient.data.remote.models.UserResponse

interface ICityApiService {

    suspend fun getCitiesByName(prefix: String): ServiceResult<CityApiResponse>
    suspend fun getCityByZip(zipCode: Int): ServiceResult<CityApiResponse>

    //suspend fun insertUser(nonce: String, jwtToken: String): ServiceResult<UserResponse>

    suspend fun getUser(nonce: String, jwtToken: String): ServiceResult<UserResponse>

    suspend fun getUser(id: Int): ServiceResult<UserResponse>

}