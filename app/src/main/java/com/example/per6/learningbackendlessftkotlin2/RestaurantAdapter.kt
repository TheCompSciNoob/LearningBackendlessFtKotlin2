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
class RestaurantAdapter(private val lifecycleOwner: LifecycleOwner, val items: LiveData<List<Restaurant>>) : RecyclerView.Adapter<RestaurantViewHolder>() {

    var onItemLongClick : ((View, Int) -> Unit)? = null

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

    override fun getItemCount(): Int {
        items.value?.size?.let {
            return it
        }
        return 0
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {

        val item = items.value?.get(position)
        holder.itemView?.apply {
            item?.let {
                restaurantName.text = it.restaurantName
                genre.text = it.genre
                address.text = it.address
                foodRating.rating = it.foodRating.toFloat()
                priceRating.rating = it.priceRating.toFloat()
            }
            setOnLongClickListener {
                onItemLongClick?.invoke(this, position)
                return@setOnLongClickListener true
            }
        }
    }
}

class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}