package com.example.mapwithtab.adpters

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.example.mapwithtab.R
import com.example.mapwithtab.databinding.ItemLocationListLayoutBinding
import com.example.mapwithtab.model.LocationTableModel

class LocationAdpter :
    BaseRecyclerViewAdapter<LocationTableModel, ItemLocationListLayoutBinding>() {

    override fun initSortedList() = SortedList(LocationTableModel::class.java, object : SortedListAdapterCallback<LocationTableModel>(this) {
        override fun compare(o1: LocationTableModel, o2: LocationTableModel) =
            o1.Name.compareTo(o2.Name)

        override fun areContentsTheSame(oldItem: LocationTableModel, newItem: LocationTableModel) =
            oldItem.Name == newItem.Name

        override fun areItemsTheSame(item1: LocationTableModel, item2: LocationTableModel) =
            item1 == item2

        })

    override fun BaseViewHolder<ItemLocationListLayoutBinding>.bindData(
        item: LocationTableModel?,
        position: Int
    ) {
        binding.apply {
            root.setOnClickListener {
                item?.let { it1 -> listener?.invoke(it, it1, position) }
            }
            data = item

        }
    }
    override fun getLayout(): Int = R.layout.item_location_list_layout
}