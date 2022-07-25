package com.example.mapwithtab.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mapwithtab.model.CountryModel
import com.example.mapwithtab.data.remote.api.APIInterface


class APIRepository(private val apiInterface: APIInterface) {

    private val _countryList: MutableLiveData<List<CountryModel>> = MutableLiveData()
    val countryList: LiveData<List<CountryModel>> get() = _countryList

    suspend fun getCountry() {
        val response = apiInterface.getAllCountry()
        if (response?.body() != null) {
            _countryList.postValue(response.body())
        }
    }
}