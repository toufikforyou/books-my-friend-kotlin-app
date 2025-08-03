package com.sopnolikhi.booksmyfriend.services.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sopnolikhi.booksmyfriend.services.includes.utils.KeyValues

object Storage  {

    private const val STORAGE_PERMISSION_REQUEST_CODE = KeyValues.STORAGE_PERMISSION_CODE

    fun hasStoragePermissions(context: Context): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager() || readPermission == PackageManager.PERMISSION_GRANTED
        } else {
            readPermission == PackageManager.PERMISSION_GRANTED || writePermission == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestStoragePermissions(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception or show a message to the user
            }
        } else
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray, callback: PermissionCallback) {
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback.onPermissionGranted()
            } else {
                callback.onPermissionDenied()
            }
        }
    }

    fun handlePermissionResult(activity: Activity, grantResults: IntArray, callback: PermissionCallback) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // User denied permission, but did not check "never ask again"
            callback.onPermissionDenied()
        } else {
            // User denied permission and checked "never ask again"
            showPermissionExplanationDialog(activity)
        }
    }

    private fun showPermissionExplanationDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("Permission Required")
            .setMessage("This app requires storage permission to function properly. You can grant the permission in the app settings.")
            .setPositiveButton("Open Settings") { _, _ -> openAppSettings(activity) }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivityForResult(intent, STORAGE_PERMISSION_REQUEST_CODE)
    }
}