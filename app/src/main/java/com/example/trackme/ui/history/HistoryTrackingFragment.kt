package com.example.trackme.ui.history

import EndlessScrollListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackme.R
import com.example.trackme.databinding.FragmentTrackingHistoryBinding
import com.github.ajalt.timberkt.Timber
import com.xinh.presentation.tracking.TrackMeViewModel
import com.xinh.share.BaseFragment
import com.xinh.share.SpacingItemDecoration
import com.xinh.share.extension.observeExt
import com.xinh.share.extension.toPx
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryTrackingFragment : BaseFragment<FragmentTrackingHistoryBinding>() {

    private val trackMeViewModel: TrackMeViewModel by viewModel()

    companion object {
        const val TAG = "HistoryTrackingFragment"

        fun newInstance(): HistoryTrackingFragment {
            return HistoryTrackingFragment()
        }
    }

    private val historyAdapter = HistoryAdapter()

    override fun getLayout(): Int {
        return R.layout.fragment_tracking_history
    }

    override fun initView() {
        viewBinding?.apply {
            adapter = historyAdapter
            itemDecoration = SpacingItemDecoration(8.toPx())
            addOnScrollListener =
                object : EndlessScrollListener(rcHistory.layoutManager as LinearLayoutManager) {
                    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                        Timber.d { "onLoadMore............." }
                        trackMeViewModel.loadMoreHistory()
                    }
                }
        }

        trackMeViewModel.getHistory()
    }

    override fun initObserveViewModel() {
        trackMeViewModel.apply {

            onShowLoadingHistory.observeExt(this@HistoryTrackingFragment) {
                viewBinding?.isShowLoading = it
            }

            onGetHistory.observeExt(this@HistoryTrackingFragment) {
                historyAdapter.setData(it)
            }

            onLoadMoreHistory.observeExt(this@HistoryTrackingFragment) {
                historyAdapter.addData(it)
            }

            onShowLoadMoreHistory.observeExt(this@HistoryTrackingFragment) {
                historyAdapter.showLoadMore(it)
            }
        }
    }

}