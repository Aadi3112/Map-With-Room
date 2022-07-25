package com.example.mapwithtab.adpters

import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.example.mapwithtab.R
import com.example.mapwithtab.databinding.ItemCountryListLayoutBinding
import com.example.mapwithtab.databinding.ItemLocationListLayoutBinding
import com.example.mapwithtab.model.CountryModel
import com.example.mapwithtab.model.LocationTableModel

class CountryListAdpter :
    BaseRecyclerViewAdapter<CountryModel, ItemCountryListLayoutBinding>() {

    override fun initSortedList() = SortedList(
        CountryModel::class.java,
        object : SortedListAdapterCallback<CountryModel>(this) {
            override fun compare(o1: CountryModel, o2: CountryModel) =
                o1.startOfWeek.compareTo(o2.name.common)

            override fun areContentsTheSame(
                oldItem: CountryModel,
                newItem: CountryModel
            ) =
                oldItem.startOfWeek == newItem.name.common

            override fun areItemsTheSame(item1: CountryModel, item2: CountryModel) =
                item1 == item2

        })

    override fun BaseViewHolder<ItemCountryListLayoutBinding>.bindData(
        item: CountryModel?,
        position: Int
    ) {
        binding.apply {
            root.setOnClickListener {
                item?.let { it1 -> listener?.invoke(it, it1, position) }
            }
            data = item

        }
    }

    override fun getLayout(): Int = R.layout.item_country_list_layout
}