package com.example.cityapiclient.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityapiclient.data.ServiceResult
import com.example.cityapiclient.data.remote.CityApiService
import com.example.cityapiclient.data.remote.CityDto
import com.example.cityapiclient.data.succeeded
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isSignedIn: Boolean = false,
    val cityPrefix: String? = "",
    val cities: List<CityDto> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cityApiService: CityApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState()
    )
    val uiState = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            _uiState.value
        )

    private var cityNameSearchJob: Job? = null

    fun onCityNameSearch(prefix: String?) {

        Log.d("debug", "searchCities prefix: $prefix")

        _uiState.update {
            it.copy(cityPrefix = prefix)
        }

        _uiState.value.cityPrefix?.let { prefix ->
            if (prefix.length > 2)

                cityNameSearchJob?.cancel()
                cityNameSearchJob = viewModelScope.launch {

                val cityResponse = cityApiService.getCitiesByName(prefix)
                    when(cityResponse) {
                        is ServiceResult.Success -> {
                            _uiState.update {
                                it.copy(cities = cityResponse.data)
                            }
                        }
                        is ServiceResult.Error -> {
                            //Log.d("debug", "api error: ${cityResponse.errors.toString()}")
                        }
                    }
            }
        }
    }
}