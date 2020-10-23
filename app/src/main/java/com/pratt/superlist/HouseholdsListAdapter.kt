package com.pratt.superlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_household_list.view.*

class HouseholdsListAdapter (var householdsListItems: List<HouseholdsModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    class HouseholdViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(householdsModel: HouseholdsModel){
            itemView.householdb.text = householdsModel.h_name
            itemView.householdidtv.text = householdsModel.h_id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_household_list,parent,false)
        return HouseholdViewHolder(view)
    }

    override fun getItemCount(): Int {
        return householdsListItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HouseholdViewHolder).bind(householdsListItems[position])
    }

}