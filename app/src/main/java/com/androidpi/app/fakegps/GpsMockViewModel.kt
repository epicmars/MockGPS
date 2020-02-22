package com.androidpi.app.fakegps

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

/**
 * Created on 2020-02-21.
 */
class GpsMockViewModel : ViewModel(){

    var _latitude = MutableLiveData<String>()

    var _longitude = MutableLiveData<String>()


    val latitude = MediatorLiveData<Double>()

    val longitude = MediatorLiveData<Double>()

    init {
        latitude.addSource(_latitude, Observer {
            latitude.value = it.toDoubleOrNull()
        })

        longitude.addSource(_longitude, Observer {
            longitude.value = it.toDoubleOrNull()
        })
    }
}