package com.example.mapwithtab.data.remote.api

import com.example.mapwithtab.model.CountryModel
import retrofit2.Response
import retrofit2.http.GET


interface APIInterface {
    @GET("all")
    suspend fun getAllCountry(): Response<List<CountryModel>>
}