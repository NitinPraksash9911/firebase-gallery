package `in`.nitin.firebaseimageupload.datasource.data

import `in`.nitin.firebaseimageupload.datasource.repo.Result
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.*


class FirebaseQueryLiveData(ref: DatabaseReference) : LiveData<Result<DataSnapshot?>>() {
    private val query: Query = ref
    private val listener = MyValueEventListener()


    override fun onActive() {
        Log.d(LOG_TAG, "onActive")
        query.addValueEventListener(listener)
    }

    override fun onInactive() {
        Log.d(LOG_TAG, "onInactive")
        query.removeEventListener(listener)
    }

    private inner class MyValueEventListener : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            value = Result.success(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            value = Result.error(databaseError.toException().message!!)
        }
    }


    companion object {
        private const val LOG_TAG = "FirebaseQueryLiveData"
    }
}
