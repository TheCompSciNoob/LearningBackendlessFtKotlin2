package com.example.per6.learningbackendlessftkotlin2

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.PopupMenu
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_restaurant_display.*

/**
 * Created by per6 on 3/2/18.
 */
class RestaurantDisplayFragment : Fragment() {

    private val restaurantsSelected: MutableList<Restaurant> = arrayListOf()
    private lateinit var restaurants: LiveData<List<Restaurant>>
    private lateinit var restaurantAdapter: RestaurantAdapter
    private var isInActionMode: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurant_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //BackendlessUser
        val user = Backendless.UserService.CurrentUser()

        //data
        val viewModel = ViewModelProviders.of(activity!!).get(BackendlessViewModel::class.java)
        restaurants = if (savedInstanceState == null) {
            viewModel.getRestaurants(user.objectId, true)
        } else {
            viewModel.getRestaurants(user.objectId, false)
        }

        //title
        activity?.title = "Your Restaurants"

        //widgets
        restaurantAdapter = RestaurantAdapter(this, restaurants).apply {
            onItemLongClick = { view, position ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.menuInflater.inflate(R.menu.options_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.delete_option ->
                            viewModel.removeRestaurant(restaurants.value!!.get(position)) {
                                when (it) {
                                    is FailedBackendResponse ->
                                        Toast.makeText(context, it.fault?.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        R.id.edit_option -> {
                            viewModel.editRestaurant = restaurants.value!!.get(position)
                            fragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer, EditFragment())?.addToBackStack(null)?.commit()
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                popupMenu.show()
            }
        }
        restaurantsRecyclerView.apply {
            adapter = restaurantAdapter
        }
        editRestaurantFAB.setOnClickListener {
            viewModel.editRestaurant = null
            fragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer, EditFragment())?.addToBackStack(null)?.commit()
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

        val viewModel = ViewModelProviders.of(activity!!).get(BackendlessViewModel::class.java)

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
                    is SuccessfulLoginResponse ->
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

    private var editRes = Restaurant()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //title
        activity?.title = "Edit Restaurant"

        //bind data
        val viewModel = ViewModelProviders.of(activity!!).get(BackendlessViewModel::class.java)
        viewModel.editRestaurant?.let {
            editRes = it
            bind(it)
        }

        //save or update
        newResButton.setOnClickListener { button ->
            button.isEnabled = false
            editRes.apply {
                restaurantName = newResName.text.toString()
                genre = newResGenre.text.toString()
                address = newResAddress.text.toString()
                foodRating = newResFoodRating.text.toString().toDouble()
                priceRating = newResPriceRating.text.toString().toDouble()

            }
            viewModel.addOrUpdateRestaurant(editRes) {
                when (it) {
                    is SuccessfulSaveOrUpdateResponse -> {
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                        fragmentManager?.popBackStack()
                    }
                    is FailedBackendResponse -> {
                        Toast.makeText(context, it.fault?.message, Toast.LENGTH_SHORT).show()
                        button.isEnabled = true
                    }
                }
            }
        }
    }

    fun bind(res: Restaurant) {
        newResName.setText(res.restaurantName)
        newResGenre.setText(res.genre)
        newResAddress.setText(res.address)
        newResFoodRating.setText(res.foodRating.toString())
        newResPriceRating.setText(res.priceRating.toString())
    }
}

class AccountFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = "Create Account"

        //widgets
        registerButton.setOnClickListener {

            if (passwordRegister.text.toString() != confirmPasswordRegister.text.toString()) {
                Toast.makeText(context, "Password does not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordRegister.text.isNotEmpty()) {

                it.isEnabled = false

                val backendlessUser = BackendlessUser()
                with(backendlessUser) {
                    email = emailRegister.text.toString()
                    password = passwordRegister.text.toString()
                    setProperties(firstNameRegister.text.toString(), lastNameRegister.text.toString(), userNameRegister.text.toString())
                }

                Backendless.UserService.register(backendlessUser, object : AsyncCallback<BackendlessUser> {
                    override fun handleFault(fault: BackendlessFault?) {
                        Toast.makeText(context, fault?.message, Toast.LENGTH_SHORT).show()
                        it.isEnabled = true
                    }

                    override fun handleResponse(response: BackendlessUser?) {
                        Toast.makeText(context, "Account Created", Toast.LENGTH_SHORT).show()
                        fragmentManager?.popBackStack()
                    }

                })
            } else {
                Toast.makeText(context, "Invalid arguments", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun BackendlessUser.setProperties(firstname: String, lastname: String, username: String) {
        setProperty("firstname", firstname)
        setProperty("lastname", lastname)
        setProperty("username", username)
    }
}