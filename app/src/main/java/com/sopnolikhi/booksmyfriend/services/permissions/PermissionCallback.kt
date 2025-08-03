package com.sopnolikhi.booksmyfriend.services.permissions

interface PermissionCallback {
    fun onPermissionGranted()
    fun onPermissionDenied()
}