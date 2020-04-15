package `in`.nitin.firebaseimageupload.datasource.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ImageData(var imageUrl: String?="", var imageName: String?="") {

}