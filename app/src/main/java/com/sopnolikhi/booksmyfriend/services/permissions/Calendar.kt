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

object Calendar {

    private const val CALENDAR_PERMISSION_REQUEST_CODE = KeyValues.CALENDER_PERMISSION_CODE

    fun hasCalendarPermissions(context: Context): Boolean {
        val readPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
        val writePermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)

        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED
    }

    fun requestCalendarPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
            CALENDAR_PERMISSION_REQUEST_CODE
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int, grantResults: IntArray, callback: PermissionCallback
    ) {
        if (requestCode == CALENDAR_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback.onPermissionGranted()
            } else {
                callback.onPermissionDenied()
            }
        }
    }

    fun handlePermissionResult(
        activity: Activity, grantResults: IntArray, callback: PermissionCallback
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.READ_CALENDAR
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.WRITE_CALENDAR
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
            .setMessage("This app requires calendar permission to function properly. You can grant the permission in the app settings.")
            .setPositiveButton("Open Settings") { _, _ -> openAppSettings(activity) }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }.show()
    }

    private fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivityForResult(intent, CALENDAR_PERMISSION_REQUEST_CODE)
    }
}