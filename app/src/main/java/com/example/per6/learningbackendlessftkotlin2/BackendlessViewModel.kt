package com.example.per6.learningbackendlessftkotlin2

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder

/**
 * Created by per6 on 3/2/18.
 */
class BackendlessViewModel : ViewModel() {

    //what we change
    private val mutableRestaurants: MutableLiveData<List<Restaurant>> = MutableLiveData()
    private var ownerId : String = ""
    var editRestaurant : Restaurant? = null

    init {
        Log.d("BackendlessViewModel", "instance created")
    }

    inline fun loginUser(username: String, password: String, crossinline onResponse: (BackendResponse) -> Unit) {

        //Backendless
        Backendless.UserService.login(username, password, object : AsyncCallback<BackendlessUser> {

            override fun handleFault(fault: BackendlessFault?) {
                onResponse(FailedBackendResponse(fault))
            }

            override fun handleResponse(response: BackendlessUser?) {
                onResponse(SuccessfulLoginResponse(response))
            }
        })
    }

    inline fun addOrUpdateRestaurant(restaurant: Restaurant, crossinline onResponse: (BackendResponse) -> Unit) {

        //Backendless
        Backendless.Data.of(Restaurant::class.java).save(restaurant, object : AsyncCallback<Restaurant> {

            override fun handleFault(fault: BackendlessFault?) {
                onResponse(FailedBackendResponse(fault))
            }

            override fun handleResponse(response: Restaurant?) {
                refreshRestaurants()
                onResponse(SuccessfulSaveOrUpdateResponse(response))
            }

        })
    }

    inline fun removeRestaurant(restaurant: Restaurant, crossinline onResponse: (BackendResponse) -> Unit) {

        //Backendless
        Backendless.Persistence.of(Restaurant::class.java).remove(restaurant, object : AsyncCallback<Long> {

            override fun handleFault(fault: BackendlessFault?) {
                onResponse(FailedBackendResponse(fault))
            }

            override fun handleResponse(response: Long?) {
                refreshRestaurants()
                onResponse(SuccessfulRemoveResponse(response))
            }

        })
    }

    fun refreshRestaurants() {

        //backendless data retrieval
        val dataQuery = DataQueryBuilder.create().apply {
            whereClause = "ownerId = '$ownerId'"
        }
        Backendless.Data.of(Restaurant::class.java).find(dataQuery, object : AsyncCallback<List<Restaurant>> {

            override fun handleFault(fault: BackendlessFault?) {
                Log.d("RestaurantLiveData: ", "data retrieval failed")
            }

            override fun handleResponse(response: List<Restaurant>?) {
                response?.let {
                    mutableRestaurants.value = it
                }
            }

        })
    }

    fun getRestaurants(newOwnderId: String, refresh: Boolean): LiveData<List<Restaurant>> {
        ownerId = newOwnderId
        if (refresh) {
            refreshRestaurants()
        }
        return mutableRestaurants
    }
}

sealed class BackendResponse

data class FailedBackendResponse(val fault: BackendlessFault?) : BackendResponse()

data class SuccessfulLoginResponse(val response: BackendlessUser?) : BackendResponse()

data class SuccessfulSaveOrUpdateResponse(val response : Restaurant?) : BackendResponse()

data class SuccessfulRemoveResponse(val respose : Long?) : BackendResponse()