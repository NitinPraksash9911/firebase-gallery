package `in`.nitin.firebaseimageupload.ui.fragment

import `in`.nitin.firebaseimageupload.BuildConfig
import `in`.nitin.firebaseimageupload.R
import `in`.nitin.firebaseimageupload.application.AppConstant.REQUEST_CODE_PERMISSIONS
import `in`.nitin.firebaseimageupload.application.AppConstant.REQUIRED_PERMISSIONS
import `in`.nitin.firebaseimageupload.application.FirebaseApplication
import `in`.nitin.firebaseimageupload.databinding.BottomDailogLayoutBinding
import `in`.nitin.firebaseimageupload.databinding.HomeFragmentBinding
import `in`.nitin.firebaseimageupload.datasource.data.ImageData
import `in`.nitin.firebaseimageupload.datasource.repo.Result
import `in`.nitin.firebaseimageupload.ui.activity.MainActivity
import `in`.nitin.firebaseimageupload.ui.adapter.ImageAdpater
import `in`.nitin.firebaseimageupload.ui.adapter.OnImageListener
import `in`.nitin.firebaseimageupload.ui.utils.*
import `in`.nitin.firebaseimageupload.viewmodel.FirebaseViewModel
import `in`.nitin.firebaseimageupload.viewmodel.ViewModelProviderFactory
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Camera
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


/**
 * Main [Fragment] subclass.
 */


class HomeFragment : Fragment(),
    OnImageListener {

    lateinit var binding: HomeFragmentBinding

    private val REQUEST_TAKE_PHOTO = 1
    private val GALLERY_REQUEST = 2


    private var imageUri: Uri? = null

    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var dailogBinding: BottomDailogLayoutBinding
    var outputDirectory: File? = null

    lateinit var imageAdpater: ImageAdpater

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    /**
     *
     * [ViewModel] that's scoped to a navigation graph,
     * enabling you to share UI-related data between the graph's destinations.
     * Any data which is shared by this view model store in this VM.
     * Any ViewModel objects created in this way live until the associated NavHost
     * and its ViewModelStore are cleared or until the navigation graph is popped from the back stack.
     * */
    private val viewmodel by navGraphViewModels<FirebaseViewModel>(R.id.nav_graph) {
        providerFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initAll()
    }

    private fun initAll() {
        showSystemUI()
        checkNetworkStatus()
        (activity as MainActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as MainActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        initViews()
        initBottomSheet()
        clickHandler()
        getImageFromDatabase()


    }

    private fun checkNetworkStatus() {
        if (FirebaseApplication.hasNetwork()) {
            binding.statusImage.show()
            binding.contentLayout.rv.show()
            binding.contentLayout.networkStatus.hide()
        } else {
            binding.statusImage.hide()
            binding.contentLayout.rv.hide()
            binding.contentLayout.networkStatus.show()
            binding.contentLayout.networkStatus.setOnClickListener {
                initAll()
            }
            getString(R.string.no_internet).snack(Color.DKGRAY, binding.root)
        }
    }

    private fun showSystemUI() {
        activity!!.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    private fun initViews() {
        binding.lifecycleOwner = this
        FirebaseApplication.getComponent()!!.inject(this)

        outputDirectory =
            getOutputDirectory(
                context!!
            )
        imageAdpater =
            ImageAdpater(this)
        binding.contentLayout.rv.apply {
            adapter = imageAdpater
            addItemDecoration(
                ItemDecorationColumns(
                    resources.getInteger(R.integer.photo_list_preview_columns),
                    resources.getDimensionPixelSize(R.dimen.photos_list_spacing),
                    true
                )
            )

        }

        waitForTransition(binding.contentLayout.rv)

    }


    private fun clickHandler() {
        binding.selectImgBtn.setOnClickListener {
            if (FirebaseApplication.hasNetwork()) {
                if (bottomSheetDialog.isShowing) {
                    bottomSheetDialog.dismiss()
                } else {
                    bottomSheetDialog.show()
                }
            } else {
                getString(R.string.no_internet).snack(Color.DKGRAY, binding.root)

            }
        }

    }

    private fun uploadImageFirebase() {
        if (imageUri != null) {

            binding.statusImage.show()

            viewmodel.uploadImage(imageUri!!)
                .observe(viewLifecycleOwner, Observer {
                    when (it.status) {
                        Result.Status.PROGRESS -> {
                            binding.contentLayout.progressHorizontal.progress = it.progress!!

                        }
                        Result.Status.SUCCESS -> {
                            /**
                             * [NotCalled] because when image upload to storage is successful
                             * then immediately upload to database request initiated
                             * */
                        }
                        Result.Status.ERROR -> {
                            it.message!!.snack(Color.RED, binding.root)
                            binding.statusImage.hide()
                        }
                        else -> {

                        }
                    }

                })
        } else {
            getString(R.string.getImage).snack(Color.LTGRAY, binding.root)

        }
    }


    private fun getImageFromDatabase() {

        /**
         * observing list when fragment restored
         * */
        viewmodel.getSavedImageList().observe(viewLifecycleOwner, Observer {
            /**
             * showing different text when list is empty or filled
             * */
            binding.contentLayout.listTv.showListStatus(it.isEmpty())

            imageAdpater.submitList(it)
        })

        /**
         * getting fresh list
         * */
        viewmodel.firebaseQueryLiveData.observe(viewLifecycleOwner, Observer {


            when (it.status) {
                Result.Status.SUCCESS -> {

                    CoroutineScope(Dispatchers.Main).launch {
                        val dataSnapshot: MutableIterable<DataSnapshot> = it.data!!.children
                        val deferredImageData = async {
                            dataSnapshot.getImageList()
                        }

                        /**
                         * setting list in adapter
                         * */
                        imageAdpater.submitList(deferredImageData.await())
                        /**
                         * Saving image list in saveStateHandle
                         * */
                        viewmodel.savedImageList(deferredImageData.await())

                        binding.statusImage.hide()
                        /**
                         * showing different text when list is empty or filled
                         * */
                        binding.contentLayout.listTv.showListStatus(
                            deferredImageData.await().isEmpty()
                        )

                    }


                }
                Result.Status.ERROR -> {
                    binding.statusImage.hide()

                    it.message!!.snack(Color.RED, binding.root)
                }
                else -> {

                }
            }


        })
    }


    private fun initBottomSheet() {
        dailogBinding = BottomDailogLayoutBinding.inflate(
            LayoutInflater.from(context),
            null, false
        )

        bottomSheetDialog =
            BottomSheetDialog(context!!).apply { setContentView(dailogBinding.root) }

        /**
         * get image from [Camera]
         * */
        dailogBinding.btnCamera.setOnClickListener {
            /**
             * check for [RuntimePermission]
             * */
            if (allPermissionsGranted()) {
                captureByCamera()
            } else {
                requestForPermission()
            }

        }

        /**
         * get image from [Gallery]
         * */
        dailogBinding.fromGallery.setOnClickListener {

            val gallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            if (allPermissionsGranted()) {
                startActivityForResult(gallery, GALLERY_REQUEST)
            } else {
                requestForPermission()

            }


        }
    }


    private fun requestForPermission() {
        requestPermissions(
            REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            /**
             * for cropping result
             * */
            val result = CropImage.getActivityResult(data)

            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    val imgFile = File(currentPhotoPath)
                    if (imgFile.exists()) {
                        imageUri = Uri.fromFile(imgFile)

                        cropImage(imageUri!!)
                    }
                }
                GALLERY_REQUEST -> {
                    imageUri = data!!.data
                    cropImage(imageUri!!)

                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    imageUri = result.uri;
                    /**
                     * when cropping successful image will upload to [Firebase]
                     * */
                    uploadImageFirebase()
                }
                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    result.error!!.message!!.snack(Color.RED, binding.root)

                }


            }


        }
        bottomSheetDialog.dismiss()

    }

    private fun cropImage(imageUri: Uri) {
        CropImage.activity(imageUri)
            .start(context!!, this)
    }

    private fun captureByCamera() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createFile(
                        outputDirectory!!,
                        FILENAME,
                        PHOTO_EXTENSION
                    )
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    "Problem $ex".snack(Color.RED, binding.root)
                    return
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context!!,
                        "${BuildConfig.APPLICATION_ID}.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                getString(R.string.proceed).showToast(context!!)
            } else {
                getString(R.string.grant_premission).showToast(context!!)
                /**
                 * if user denied the [Permissions] then s/he has to accept this manually in application setting
                 * */
                goToSettings()
            }
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context!!, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun goToSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${context!!.packageName}")
        ).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }
    }


    override fun onImageClick(imageData: ImageData?, imageView: ImageView) {
        val destination =
            HomeFragmentDirections.actionHomeFragmentToSingleImageFragment(imageUrl = imageData!!.imageUrl!!)

        imageView.transitionName
        val extras = FragmentNavigatorExtras(

            /**
             * Returns the name of the View to be used to identify Views in Transitions.
             * */
            imageView.toTransitionGroup()

        )

        /**
         * @param extras for shared element transition
         * @param destination to open [SingleImageFragment]
         * */
        findNavController().navigate(destination, extras)
    }


    companion object {
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"

        lateinit var currentPhotoPath: String

        /** Helper function used to create a timestamped file */
        @Throws(IOException::class)
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(
                baseFolder, SimpleDateFormat(format, Locale.US)
                    .format(System.currentTimeMillis()) + extension
            ).apply {
                currentPhotoPath = this.absolutePath
            }

        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.externalMediaDirs.firstOrNull()?.let {
                    File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
                }
            } else {
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }


    }

}
