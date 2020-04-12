package com.xinh.share

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<D, V : ViewDataBinding, VH : BaseViewHolder<D, V>>(
    private val data: MutableList<D> = ArrayList()
) : RecyclerView.Adapter<VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return onCreateViewHolderFactory(parent, viewType)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(data[position], position)
    }

    abstract fun onCreateViewHolderFactory(parent: ViewGroup, viewType: Int): VH

    override fun getItemCount(): Int = data.size

    fun setData(data: List<D>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun getData() = data
}

abstract class BaseViewHolder<D, V : ViewDataBinding>(val binding: V) :
    RecyclerView.ViewHolder(binding.root) {

    fun context(): Context {
        return binding.root.context
    }

    fun getString(@StringRes resId: Int): String {
        return context().getString(resId)
    }

    fun getString(@StringRes resId: Int?, default: String = ""): String {
        if (resId == null) return default

        return context().getString(resId)
    }

    abstract fun bind(item: D, position: Int)
}

class SpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildViewHolder(view).adapterPosition
        val itemCount = state.itemCount
        val layoutManager = parent.layoutManager
        setSpacingForDirection(outRect, layoutManager, position, itemCount)
    }

    private fun setSpacingForDirection(
        outRect: Rect,
        layoutManager: RecyclerView.LayoutManager?,
        position: Int,
        itemCount: Int
    ) {

        when (getDisplayMode(layoutManager)) {
            HORIZONTAL -> {
                outRect.left = spacing
                outRect.right = if (position == itemCount - 1) spacing else 0
                outRect.top = 0
                outRect.bottom = 0
            }
            VERTICAL -> {
                outRect.left = spacing
                outRect.right = spacing
                outRect.top = spacing
                outRect.bottom = if (position == itemCount - 1) spacing else 0
            }
            GRID -> if (layoutManager is GridLayoutManager) {
                val gridLayoutManager = layoutManager as GridLayoutManager?
                val cols = gridLayoutManager!!.spanCount
                val rows = itemCount / cols

                outRect.left = spacing
                outRect.right = if (position % cols == cols - 1) spacing else 0
                outRect.top = spacing
                outRect.bottom = if (position / cols == rows - 1) spacing else 0
            }
        }
    }

    private fun getDisplayMode(layoutManager: RecyclerView.LayoutManager?): Int {
        if (layoutManager is GridLayoutManager) return GRID
        return if (layoutManager!!.canScrollHorizontally()) HORIZONTAL else VERTICAL
    }

    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
        const val GRID = 2
    }
}