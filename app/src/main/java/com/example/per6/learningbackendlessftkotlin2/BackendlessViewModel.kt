package com.example.per6.learningbackendlessftkotlin2

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault

/**
 * Created by per6 on 3/2/18.
 */
class BackendlessViewModel : ViewModel() {

    init {
        Log.d("BackendlessViewModel", "instance created")
    }

    inline fun loginUser(username : String, password : String, crossinline onResponse: (BackendResponse) -> Unit) {

        //Backendless
        Backendless.UserService.login(username, password, object : AsyncCallback<BackendlessUser> {
            override fun handleFault(fault: BackendlessFault?) {
                onResponse(FailedBackendResponse(fault))
            }

            override fun handleResponse(response: BackendlessUser?) {
                onResponse(SuccessBackendResponse(response))
            }
        })
    }
}

class RestaurantLiveData(list : List<Restaurant>) : MutableLiveData<List<Restaurant>>(), List<Restaurant> by list {}

sealed class BackendResponse

data class FailedBackendResponse(val fault: BackendlessFault?) : BackendResponse()

data class SuccessBackendResponse(val response : BackendlessUser?) : BackendResponse()