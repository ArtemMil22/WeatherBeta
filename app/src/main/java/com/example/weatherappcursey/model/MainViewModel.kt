package com.example.weatherappcursey.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel() {

    val liveDataCurrent = MutableLiveData<WeatherModel>()
    // сюда и нужно передать наш дата класс который будет содержать нужную информацию
        // Здесь мы и будем хранить и обновлять элемент главный элемент верхнюю карточку
        // всю инфу в этой  одной карточке что сверху
    val liveDataList = MutableLiveData<List<WeatherModel>>()
}