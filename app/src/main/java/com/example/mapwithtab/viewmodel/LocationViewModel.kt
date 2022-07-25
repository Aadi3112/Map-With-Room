package com.example.mapwithtab.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapwithtab.model.CountryModel
import com.example.mapwithtab.model.LocationTableModel
import com.example.mapwithtab.repository.APIRepository
import com.example.mapwithtab.repository.LocationRepository
import kotlinx.coroutines.launch

class LocationViewModel(private val apiRepository: APIRepository) : ViewModel() {

    var liveDataLocation: LiveData<List<LocationTableModel>>? = null
    val liveDataCountry: LiveData<List<CountryModel>> get() = apiRepository.countryList

    fun insertData(
        context: Context,
        name: String,
        email: String,
        phone: String,
        latlang: String,
        diffrence: String
    ) {
        viewModelScope.launch {
            LocationRepository.insertData(context, name, email, phone, latlang, diffrence)
        }

    }

    fun getLocationDetails(context: Context): LiveData<List<LocationTableModel>>? {
        liveDataLocation = LocationRepository.getLoginDetails(context)
        return liveDataLocation
    }

    fun getAllCountry(){
        viewModelScope.launch {
            apiRepository.getCountry()
        }
    }


}