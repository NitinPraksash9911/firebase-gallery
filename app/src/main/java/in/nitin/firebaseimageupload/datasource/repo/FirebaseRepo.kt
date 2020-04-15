package `in`.nitin.firebaseimageupload.datasource.repo

import `in`.nitin.firebaseimageupload.datasource.data.ImageData
import `in`.nitin.firebaseimageupload.datasource.data.FirebaseQueryLiveData
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.schedule

@Singleton
class FirebaseRepo @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val storageReference: StorageReference
) {

    private val uploadLiveData = MutableLiveData<Result<UploadTask.TaskSnapshot>>()

    private var queryLiveData = FirebaseQueryLiveData(databaseReference)

    var mUploadTask: UploadTask? = null

    fun getDataSnapshotLiveData(): FirebaseQueryLiveData {
        return queryLiveData
    }


    fun uploadToStorage(imageURI: Uri): LiveData<Result<UploadTask.TaskSnapshot>> {

        val metadata =
            StorageMetadata.Builder().setCustomMetadata("contentType", "image/jpg").build()

        val imageFileRef = storageReference.child("${System.currentTimeMillis()}.jpg")

        mUploadTask = imageFileRef.putFile(imageURI, metadata)

        mUploadTask!!.addOnSuccessListener {

            Timer().schedule(1500) {
                uploadLiveData.postValue(Result.progress(0))
            }

        }.addOnFailureListener {

            uploadLiveData.value = Result.error(it.message!!)

        }.addOnProgressListener {

            uploadLiveData.value =
                Result.progress((100.0 * it.bytesTransferred / it.totalByteCount).toInt())

        }.addOnCompleteListener {
            if (it.isSuccessful) {

                imageFileRef.downloadUrl.addOnSuccessListener {

                    /*if storage upload successful then update the database*/
                    uploadToDatabase(it.toString())
                }

            } else {

                uploadLiveData.value = Result.error(it.exception!!.message!!)
            }
        }

        return uploadLiveData
    }

    private fun uploadToDatabase(imageFile: String) {
        val imageId = databaseReference.push().key
        val data = ImageData(imageFile, "${imageId!!.substring(6)}imageName")
        databaseReference.child(imageId).setValue(data)
    }


}