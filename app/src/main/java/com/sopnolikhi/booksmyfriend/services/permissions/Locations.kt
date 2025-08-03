package com.sopnolikhi.booksmyfriend.services.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sopnolikhi.booksmyfriend.services.includes.utils.KeyValues

object Locations {

    private const val LOCATION_PERMISSION_REQUEST_CODE = KeyValues.LOCATION_PERMISSION_CODE
    private const val REQUEST_ENABLE_GPS = KeyValues.REQUEST_ENABLE_GPS_CODE

    fun hasLocationPermissions(context: Context): Boolean {
        val fineLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    fun requestGPSEnabled(activity: Activity) {
        activity.startActivityForResult(
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_GPS
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int, grantResults: IntArray, callback: PermissionCallback,
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback.onPermissionGranted()
            } else {
                callback.onPermissionDenied()
            }
        }
    }

    fun handlePermissionResult(
        activity: Activity, grantResults: IntArray, callback: PermissionCallback,
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // User denied permission, but did not check "never ask again"
            callback.onPermissionDenied()
        } else {
            // User denied permission and checked "never ask again"
            showPermissionExplanationDialog(activity)
        }
    }

    private fun showPermissionExplanationDialog(activity: Activity) {
        AlertDialog.Builder(activity).setTitle("Permission Required")
            .setMessage("This app requires location permission to function properly. You can grant the permission in the app settings.")
            .setPositiveButton("Open Settings") { _, _ -> openAppSettings(activity) }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }.show()
    }

    private fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivityForResult(intent, LOCATION_PERMISSION_REQUEST_CODE)
    }
}