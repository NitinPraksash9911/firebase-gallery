package `in`.nitin.firebaseimageupload.ui.fragment

import `in`.nitin.firebaseimageupload.databinding.FragmentSingleImageBinding
import `in`.nitin.firebaseimageupload.ui.utils.ZoomLayout
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater


/**
 * SingleImage [Fragment] subclass.
 */
class SingleImageFragment : Fragment() {

    private val args: SingleImageFragmentArgs by navArgs()
    lateinit var binding: FragmentSingleImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSingleImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.url = args.imageUrl

        binding.closeBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.frameLayout.onPinchZoomListener(object : ZoomLayout.PinchZoomListener {
            override fun onPinchZoom(zoom: Float) {
                /**
                 * [closeBtn] will be fade-in and fade-out when the Image View zoom-in and zoom-out accordingly
                 * */
                binding.closeBtn.alpha = 2-zoom
            }

        })

        binding.executePendingBindings()

    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        activity!!.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

}
