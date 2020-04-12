package com.xinh.share.extension

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView


@BindingAdapter("addOnScrollListener")
fun addOnScrollListener(
    view: RecyclerView,
    scrollListener: RecyclerView.OnScrollListener
) {
    view.addOnScrollListener(scrollListener)
}

@BindingAdapter("adapter")
fun setRecyclerViewAdapter(
    view: RecyclerView,
    adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?
) {
    if (adapter != null) {
        view.adapter = adapter
    }
}

@BindingAdapter("itemDecoration")
fun addDecorate(view: RecyclerView, item: RecyclerView.ItemDecoration?) {
    item?.let { view.addItemDecoration(it) }
}