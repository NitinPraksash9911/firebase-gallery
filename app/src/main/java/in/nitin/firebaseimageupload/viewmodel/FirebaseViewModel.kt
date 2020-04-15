package `in`.nitin.firebaseimageupload.viewmodel

import `in`.nitin.firebaseimageupload.datasource.repo.Result
import `in`.nitin.firebaseimageupload.datasource.data.ImageData
import `in`.nitin.firebaseimageupload.datasource.repo.FirebaseRepo
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.UploadTask
import java.util.*
import javax.inject.Inject


/**
 * @param SavedStateHandle is a handle which is in Key-Value map that will let you read
 * and write objects to and from the saved state and it will call first
 * @param FirebaseRepo use to get/set data from/to firebase
 * */
class FirebaseViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepo,
    private val stateHandle: SavedStateHandle
) : ViewModel() {

    val firebaseQueryLiveData = firebaseRepo.getDataSnapshotLiveData()

    /**
     *  for saving image list
     * */
    private val SAVE_IMAGE_LIST = "imageList"

    fun uploadImage(imageUir: Uri): LiveData<Result<UploadTask.TaskSnapshot>> {
        return firebaseRepo.uploadToStorage(imageUir)
    }


    /**
     * getting image list when fragment restored
     * */
    fun getSavedImageList(): LiveData<ArrayList<ImageData>> {
        return stateHandle.getLiveData(SAVE_IMAGE_LIST)
    }

    /**
     * saving image list to [SavedStateHandle] and it will use when fragment is restored
     * */
    fun savedImageList(savedImageData: ArrayList<ImageData>) {
        stateHandle.set(SAVE_IMAGE_LIST, savedImageData)
    }


    override fun onCleared() {
        super.onCleared()
    }
}