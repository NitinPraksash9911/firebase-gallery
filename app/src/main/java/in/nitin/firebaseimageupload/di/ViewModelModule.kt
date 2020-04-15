package `in`.nitin.firebaseimageupload.di

import `in`.nitin.firebaseimageupload.viewmodel.FirebaseViewModel
import `in`.nitin.firebaseimageupload.viewmodel.ViewModelProviderFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(FirebaseViewModel::class)
    abstract fun bindWebViewModel(myViewModel: FirebaseViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelProviderFactory): ViewModelProvider.Factory


}

