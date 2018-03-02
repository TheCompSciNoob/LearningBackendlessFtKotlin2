package com.example.per6.learningbackendlessftkotlin2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.backendless.Backendless

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = Backendless.UserService.CurrentUser()
        if (user == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, LoginFragment()).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, RestaurantDisplayFragment()).commit()
        }
    }
}
