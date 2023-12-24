package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.content.Intent
import android.app.PendingIntent
import android.R.attr.radius
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment.GeofencingConstants.ACTION_GEOFENCE
import org.koin.android.ext.android.inject


class SaveReminderFragment : BaseFragment() {

    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private var gfClient: GeofencingClient? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val layoutId = R.layout.fragment_save_reminder
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        setDisplayHomeAsUpEnabled(true)
        binding.viewModel = _viewModel
        return binding.root
    }

    private fun isCheckPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION).plus(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            _viewModel.navigationCommand.value = NavigationCommand.To(
                SaveReminderFragmentDirections
                    .actionSaveReminderFragmentToSelectLocationFragment()
            )
        }
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE

        binding.saveReminder.setOnClickListener {
            if (isCheckPermission()) {
                gfClient = LocationServices.getGeofencingClient(requireActivity())

                gfClient?.addGeofences(
                    GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                        .addGeofence(
                            Geofence.Builder()
                                .setRequestId("geofence_id")
                                .setCircularRegion(
                                    _viewModel.latitude.value ?: 0.0,
                                    _viewModel.longitude.value ?: 0.0,
                                    radius.toFloat()
                                )
                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                                .build()
                        )
                        .build(), PendingIntent.getBroadcast(
                        requireContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )?.run {
                    addOnSuccessListener {
                        _viewModel.validateAndSaveReminder(
                            ReminderDataItem(
                                _viewModel.reminderTitle.value,
                                _viewModel.reminderDescription.value,
                                _viewModel.reminderSelectedLocationStr.value,
                                _viewModel.latitude.value,
                                _viewModel.longitude.value
                            )
                        )
                    }
                    addOnFailureListener {
                        _viewModel.showErrorMessage.value =
                            resources.getString(R.string.error_adding_geofence)
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _viewModel.onClear()
    }

    internal object GeofencingConstants {
        internal const val ACTION_GEOFENCE =
            "locationreminders.geofence.action.ACTION_GEOFENCE_EVENT"
    }
}