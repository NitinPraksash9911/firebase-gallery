package `in`.nitin.firebaseimageupload.application

import android.Manifest

object AppConstant {
    const val FIREBASE_STORAGE_NAME = "images"
     const val REQUEST_CODE_PERMISSIONS = 0X121

    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}