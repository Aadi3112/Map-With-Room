package com.example.mapwithtab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mapwithtab.repository.APIRepository


class LocationViewModelFactory(private val apiRepository: APIRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationViewModel(apiRepository) as T
    }

}