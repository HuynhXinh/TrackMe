package com.example.trackme.ui.tracking

import com.example.trackme.R
import com.example.trackme.databinding.FragmentTrackingBinding
import com.example.trackme.ui.OnPermissionApprovedListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.xinh.domain.model.TrackingState
import com.xinh.permission.PermissionEnum
import com.xinh.permission.PermissionUtils
import com.xinh.presentation.tracking.TrackMeViewModel
import com.example.trackme.mapper.toLatLng
import com.example.trackme.mapper.toLatLngs
import com.xinh.share.BaseFragment
import com.xinh.share.extension.observeExt
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackingFragment : BaseFragment<FragmentTrackingBinding>(), OnMapReadyCallback,
    OnPermissionApprovedListener {

    private val trackMeViewModel: TrackMeViewModel by viewModel()

    private val mapHelper = MapHelperImpl()

    companion object {
        const val TAG = "TrackingFragment"

        fun newInstance(): TrackingFragment {
            return TrackingFragment()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_tracking
    }

    override fun initView() {
        initMap()

        trackMeViewModel.getRoute()
    }

    private fun initMap() {
        mapHelper.init(childFragmentManager.findFragmentById(R.id.supportMapFragment) as? SupportMapFragment) {
            mapHelper.enableMyLocation(isPermissionApproved())
        }
    }

    private fun isPermissionApproved(): Boolean {
        return listOf(
            PermissionEnum.ACCESS_COARSE_LOCATION,
            PermissionEnum.ACCESS_FINE_LOCATION
        )
            .map { PermissionUtils.isGranted(requireContext(), it) }
            .all { it }
    }

    override fun initObserveViewModel() {
        trackMeViewModel.apply {
            onStartTracking.observeExt(this@TrackingFragment) {
                mapHelper.zoom(it.toLatLng(), 15F)
                mapHelper.showStartMarker(requireContext(), it.toLatLng())
            }

            onRoutes.observeExt(this@TrackingFragment) {
                mapHelper.showRoutes(it.toLatLngs())
            }

            onAppendRoute.observeExt(this@TrackingFragment) {
                mapHelper.appendRoute(it.first.toLatLng(), it.second.toLatLng())
                mapHelper.zoom(it.second.toLatLng(), 15F)
            }

            onCurrentStateChange.observeExt(this@TrackingFragment) {
                if (it == TrackingState.Done) {
                    mapHelper.clear()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
    }

    override fun onPermissionApproved() {
        mapHelper.enableMyLocation(isPermissionApproved())
    }
}