package com.sopnolikhi.booksmyfriend.services.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sopnolikhi.booksmyfriend.services.includes.utils.KeyValues

object Nearby {

    private const val NEARBY_DEVICES_PERMISSION_REQUEST_CODE =
        KeyValues.NEARBY_DEVICES_PERMISSION_CODE

    fun hasNearbyPermissions(context: Context): Boolean {
        val bluetoothPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val connectPermission =
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            return bluetoothPermission == PackageManager.PERMISSION_GRANTED && connectPermission == PackageManager.PERMISSION_GRANTED
        }

        return bluetoothPermission == PackageManager.PERMISSION_GRANTED
    }

    fun requestNearbyPermissions(context: Context) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.BLUETOOTH)
        }

        ActivityCompat.requestPermissions(
            context as Activity, permissions, NEARBY_DEVICES_PERMISSION_REQUEST_CODE
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int, grantResults: IntArray, callback: PermissionCallback,
    ) {
        if (requestCode == NEARBY_DEVICES_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback.onPermissionGranted()
            } else {
                callback.onPermissionDenied()
            }
        }
    }
}