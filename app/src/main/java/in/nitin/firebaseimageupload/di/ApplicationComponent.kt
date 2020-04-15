package `in`.nitin.firebaseimageupload.di

import `in`.nitin.firebaseimageupload.ui.activity.MainActivity
import `in`.nitin.firebaseimageupload.ui.fragment.HomeFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ViewModelModule::class, FirebaseModule::class]
)
interface ApplicationComponent {
    fun inject(mainActivity: MainActivity)

    fun inject(fragment: HomeFragment)


}