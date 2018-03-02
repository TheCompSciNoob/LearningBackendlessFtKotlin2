package com.example.per6.learningbackendlessftkotlin2

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by per6 on 3/2/18.
 */

@SuppressLint("ParcelCreator")
@Parcelize
data class Restaurant(var restaurantName: String = "",
                      var genre : String = "",
                      var address : String = "",
                      var priceRating : Double = 0.0,
                      var foodRating : Double = 0.0) : Parcelable {
    var objectId : String? = null
}