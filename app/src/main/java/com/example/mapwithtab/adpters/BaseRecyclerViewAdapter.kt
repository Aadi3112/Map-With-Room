package com.example.mapwithtab.adpters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback

abstract class BaseRecyclerViewAdapter<T : Any, VB : ViewDataBinding>
    : RecyclerView.Adapter<BaseViewHolder<VB>>() {

    val items: SortedList<T> by lazy {
        initSortedList()
    }

    abstract fun initSortedList(): SortedList<T>


    fun addItem(item: T) {
        items.add(item)
    }

    fun addItems(items: List<T>) {
        this.items.addAll(items)
    }

    fun removeItem(index: Int) {
        if (items.size() == 0) {
            return
        }
        items.remove(items.get(index))
    }

    var listener: ((view: View, item: T, position: Int) -> Unit)? = null

    abstract fun getLayout(): Int

    override fun getItemCount() = items.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BaseViewHolder<VB>(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            getLayout(),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        val item = items[position]
        holder.bindData(item, position)
        holder.binding.bindData(item, position)
        holder.setIsRecyclable(false)
    }

    open fun BaseViewHolder<VB>.bindData(item: T?, position: Int) {
        //Leave empty for now.
    }

    open fun VB.bindData(item: T, positon: Int) {
        //Leave empty for now.
    }


    val sortedList = SortedList(
        String::class.java, object : SortedListAdapterCallback<String>(this) {
            override fun compare(o1: String, o2: String) =
                o1.compareTo(o2)

            override fun areContentsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem

            override fun areItemsTheSame(item1: String, item2: String) = item1 == item2

        }
    )
}

class BaseViewHolder<VB : ViewDataBinding>(val binding: VB) :
    RecyclerView.ViewHolder(binding.root)

