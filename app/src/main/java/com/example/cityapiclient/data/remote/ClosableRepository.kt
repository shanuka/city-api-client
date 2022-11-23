package com.example.cityapiclient.data.remote

import android.util.Log
import com.example.cityapiclient.BuildConfig
import com.example.cityapiclient.data.ServiceResult
import com.example.cityapiclient.data.remote.models.CityApiResponse
import com.example.cityapiclient.domain.AppHttpClient
import com.example.cityapiclient.util.toCityApiError
import io.ktor.client.call.*
import io.ktor.client.request.*

class ClosableRepository {

    companion object {

        private const val BASE_URL = "http://${BuildConfig.KTOR_IP_ADDR}:8080"
        const val CITIES = "$BASE_URL/cities"
        const val CITY_BY_ZIP = "$BASE_URL/cities/zip"
        const val USER_BY_JWT = "$BASE_URL/users/authenticate"
        const val USER_BY_ID = "$BASE_URL/users"

        private const val API_KEY = "Pr67HTHS4VIP1eN"

        private val api = AppHttpClient

    }
    suspend fun getCitiesByName(prefix: String): ServiceResult<CityApiResponse> {

        Log.d("debug", "httpclientId: ${api.client()}")

        return try {
            val cityApiResponse: CityApiResponse = api.client().get(CityApiService.CITIES) {
                headers {
                    append("x-api-key", API_KEY)
                }
                url {
                    parameters.append("name", prefix)
                }
            }.body()

            ServiceResult.Success(cityApiResponse)

        }
        catch (apiError: Exception) {

            val parsedError = apiError.toCityApiError<CityApiResponse>()
            Log.d("debug", parsedError.toString())
            parsedError

        }
    }

    fun close() = api.close()
}