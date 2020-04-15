package `in`.nitin.firebaseimageupload.ui.adapter

import `in`.nitin.firebaseimageupload.databinding.ItemLayoutBinding
import `in`.nitin.firebaseimageupload.datasource.data.ImageData
import `in`.nitin.firebaseimageupload.ui.fragment.HomeFragmentDirections
import `in`.nitin.firebaseimageupload.ui.utils.toTransitionGroup
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ImageAdpater(private val onImageListener: OnImageListener) :
    ListAdapter<ImageData, ImageAdpater.ImageViewHolder>(
        ImageDataDiffCallback()
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onImageListener
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ImageViewHolder(
        private val binding: ItemLayoutBinding,
        private val onImageListener: OnImageListener
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(item: ImageData) {
            binding.imagedata = item

            /**
             * item click registration
             * */
            binding.imageView.setOnClickListener(this)

            binding.executePendingBindings()
        }

        override fun onClick(v: View?) {

            onImageListener.onImageClick(binding.imagedata,binding.imageView)

        }
    }
}

class ImageDataDiffCallback : DiffUtil.ItemCallback<ImageData>() {
    override fun areItemsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
        return oldItem.imageName == newItem.imageName
    }

    override fun areContentsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
        return oldItem == newItem
    }
}


/**
 * [RecyclerView] item click listener
 * @param imageData for showing image in another fragment
 * @param ImageView for shared transition animation
 *
 * */
interface OnImageListener {
    fun onImageClick(imageData: ImageData?, imageView: ImageView)
}
