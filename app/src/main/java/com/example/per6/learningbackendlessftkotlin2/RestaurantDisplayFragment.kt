package com.example.per6.learningbackendlessftkotlin2

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_restaurant_display.*

/**
 * Created by per6 on 3/2/18.
 */
class RestaurantDisplayFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurant_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = "Your Restaurants"

        restaurantsRecyclerView.apply {

        }
    }
}

class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = "Login"

        val viewModel = ViewModelProviders.of(this).get(BackendlessViewModel::class.java)

        val underlineText = SpannableString("Create Account")
        underlineText.setSpan(UnderlineSpan(), 0, underlineText.length, 0)

        createAccount.setText(underlineText, TextView.BufferType.SPANNABLE)
        createAccount.setOnClickListener {
            fragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer, AccountFragment())?.addToBackStack(null)?.commit()
        }

        login.setOnClickListener {
            loginFlipper.showNext()
            viewModel.loginUser(userNameLogin.text.toString(), passwordLogin.text.toString()) {
                when (it) {
                    is SuccessBackendResponse ->
                            fragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer, RestaurantDisplayFragment())?.commit()
                    is FailedBackendResponse ->
                            Toast.makeText(context, it.fault?.message, Toast.LENGTH_SHORT).show()
                }
                loginFlipper.showPrevious()
            }
        }
    }
}

class EditFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = "Edit Restaurant"
    }
}

class AccountFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = "Create Account"
    }
}