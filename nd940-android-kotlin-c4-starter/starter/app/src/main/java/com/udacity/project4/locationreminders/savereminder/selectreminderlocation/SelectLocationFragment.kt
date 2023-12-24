package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.os.Bundle
import android.view.*
import java.util.Locale
import android.content.pm.PackageManager
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback, OnClickListener {

    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private var marker: Marker? = null
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val layoutId = R.layout.fragment_select_location
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.frgMap) as SupportMapFragment

        supportMapFragment.getMapAsync(this)

        binding.btnSave.setOnClickListener(this)
        return binding.root
    }


    override fun onClick(v: View?) {
        if (v?.id == binding.btnSave.id) {
            marker?.let {
                _viewModel.longitude.value = it.position.longitude
                _viewModel.reminderSelectedLocationStr.value = it.title
                _viewModel.latitude.value = it.position.latitude
            }

            findNavController().popBackStack()
        }
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "inflater.inflate(R.menu.map_options, menu)",
            "com.udacity.project4.R"
        )
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }

        R.id.hybrid_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }

        R.id.satellite_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }

        R.id.terrain_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(ggMap: GoogleMap) {
        googleMap = ggMap
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.map_style
            )
        )
        googleMap.setOnMapLongClickListener {
            marker?.remove()
            marker = googleMap.addMarker(
                MarkerOptions()
                    .title(getString(R.string.dropped_pin))
                    .position(it)
                    .snippet(
                        String.format(
                            Locale.getDefault(),
                            getString(R.string.lat_long_snippet),
                            it.latitude,
                            it.longitude
                        )
                    )
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    it,
                    15f
                )
            )
            marker?.showInfoWindow()
        }
        googleMap.setOnPoiClickListener {
            marker?.remove()
            marker = googleMap.addMarker(
                MarkerOptions()
                    .title(it.name)
                    .position(it.latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
            marker?.showInfoWindow()
        }
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            googleMap.isMyLocationEnabled = true

            LocationServices
                .getFusedLocationProviderClient(requireContext())
                .lastLocation.addOnCompleteListener(requireActivity()) {
                    if (it.isSuccessful) {
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(it.result.latitude, it.result.longitude),
                                15f
                            )
                        )
                        marker?.remove()
                        marker = googleMap.addMarker(
                            MarkerOptions()
                                .title(getString(R.string.dropped_pin))
                                .position(LatLng(it.result.latitude, it.result.longitude))
                                .snippet(
                                    String.format(
                                        Locale.getDefault(),
                                        getString(R.string.lat_long_snippet),
                                        LatLng(it.result.latitude, it.result.longitude).latitude,
                                        LatLng(it.result.latitude, it.result.longitude).longitude
                                    )
                                )
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        )
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(it.result.latitude, it.result.longitude),
                                15f
                            )
                        )
                        marker?.showInfoWindow()
                    }
                }
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }
}