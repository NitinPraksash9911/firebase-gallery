package `in`.nitin.firebaseimageupload.di

import `in`.nitin.firebaseimageupload.application.AppConstant.FIREBASE_STORAGE_NAME
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseModule {


    @FirebaseInfo
    private val firebaseName = FIREBASE_STORAGE_NAME

    @Singleton
    @Provides
    fun provideFirebaseDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference(firebaseName)
    }

    @Singleton
    @Provides
    fun provideFirebaseStorageReference(): StorageReference {
        return FirebaseStorage.getInstance().getReference(firebaseName)

    }

    @Provides
    fun provideSaveState(): SavedStateHandle {
        return SavedStateHandle()
    }
}