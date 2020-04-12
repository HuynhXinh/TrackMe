package com.example.trackme.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.trackme.R
import com.example.trackme.databinding.ItemHistoryBinding
import com.github.ajalt.timberkt.Timber
import com.xinh.domain.model.ITEM_LOAD_MORE_TYPE
import com.xinh.domain.model.ItemHistory
import com.xinh.share.BaseAdapter
import com.xinh.share.BaseViewHolder
import com.xinh.share.GlideApp
import java.util.concurrent.atomic.AtomicBoolean

class HistoryAdapter :
    BaseAdapter<ItemHistory, ViewDataBinding, BaseViewHolder<ItemHistory, ViewDataBinding>>() {

    override fun getItemViewType(position: Int): Int {
        return getData()[position].itemType
    }

    override fun onCreateViewHolderFactory(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemHistory, ViewDataBinding> {
        return (if (viewType == ITEM_LOAD_MORE_TYPE) {
            ItemLoadMoreViewHolder(parent)
        } else ItemHistoryViewHolder(parent)) as BaseViewHolder<ItemHistory, ViewDataBinding>
    }

    fun addData(items: List<ItemHistory>) {
        val positionStart = getData().size
        getData().addAll(items)
        notifyItemRangeInserted(positionStart, items.size)
    }

    fun showLoadMore(isShow: Boolean) {
        removeLoadMore()

        if (isShow) {
            getData().add(createItemLoadMore())
            notifyItemInserted(getData().size)
        }
    }

    private fun removeLoadMore() {
        if (getData()[getData().size - 1].itemType == ITEM_LOAD_MORE_TYPE) {
            getData().removeAt(getData().size - 1)
            notifyItemRemoved(getData().size)
        }
    }

    private fun createItemLoadMore(): ItemHistory {
        return ItemHistory(
            id = " ",
            staticMap = "",
            distance = "",
            avgSpeed = "",
            time = "",
            itemType = ITEM_LOAD_MORE_TYPE
        )
    }
}

class ItemHistoryViewHolder(parent: ViewGroup) : BaseViewHolder<ItemHistory, ItemHistoryBinding>(
    DataBindingUtil.inflate(
        LayoutInflater.from(parent.context),
        R.layout.item_history,
        parent,
        false
    )
) {
    override fun bind(item: ItemHistory, position: Int) {
        binding.apply {

            Timber.d { "Map static: ${item.staticMap}" }
            Glide.with(context())
                .load(item.staticMap)
                .centerCrop()
                .into(mapView)

            tvValueDistance.text = item.distance
            tvValueSeep.text = item.avgSpeed
            tvTime.text = item.time
        }
    }
}

class ItemLoadMoreViewHolder(parent: ViewGroup) : BaseViewHolder<ItemHistory, ItemHistoryBinding>(
    DataBindingUtil.inflate(
        LayoutInflater.from(parent.context),
        R.layout.item_load_more,
        parent,
        false
    )
) {
    override fun bind(item: ItemHistory, position: Int) {
    }
}


