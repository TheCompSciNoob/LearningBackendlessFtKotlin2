package com.example.per6.learningbackendlessftkotlin2

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.restaurant_recyclerview_item.view.*

/**
 * Created by per6 on 3/2/18.
 */
class RestaurantAdapter(private val lifecycleOwner: LifecycleOwner, val items : RestaurantLiveData) : RecyclerView.Adapter<RestaurantViewHolder>() {

    init {
        items.observe(lifecycleOwner, Observer<List<Restaurant>> {
            notifyDataSetChanged()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rootView = inflater.inflate(R.layout.restaurant_recyclerview_item, parent, false)
        return RestaurantViewHolder(rootView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {

        val item = items[position]
        holder.itemView?.apply {
            restaurantName.text = item.restaurantName
            genre.text = item.genre
            address.text = item.address
            foodRating.rating = item.foodRating.toFloat()
            priceRating.rating = item.priceRating.toFloat()
        }
    }
}

class RestaurantViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {}