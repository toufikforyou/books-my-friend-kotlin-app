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

object Contact {

    private const val CONTACT_PERMISSION_REQUEST_CODE = KeyValues.CONTACT_PERMISSION_CODE

    fun hasContactPermissions(context: Context): Boolean {
        val readPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
        return readPermission == PackageManager.PERMISSION_GRANTED
    }

    fun requestContactPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_CONTACTS),
            CONTACT_PERMISSION_REQUEST_CODE
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray,
        callback: PermissionCallback
    ) {
        if (requestCode == CONTACT_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback.onPermissionGranted()
            } else {
                callback.onPermissionDenied()
            }
        }
    }

    fun handlePermissionResult(
        activity: Activity,
        grantResults: IntArray,
        callback: PermissionCallback
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.READ_CONTACTS
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
            .setMessage("This app requires contact permission to function properly. You can grant the permission in the app settings.")
            .setPositiveButton("Open Settings") { _, _ -> openAppSettings(activity) }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }.show()
    }

    private fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivityForResult(intent, CONTACT_PERMISSION_REQUEST_CODE)
    }
}