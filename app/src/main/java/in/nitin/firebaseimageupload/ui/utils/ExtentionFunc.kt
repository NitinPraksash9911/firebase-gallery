package `in`.nitin.firebaseimageupload.ui.utils

import `in`.nitin.firebaseimageupload.datasource.data.ImageData
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot


fun String.snack(color: Int, view: View) {
    val snackbar = Snackbar.make(view, this, Snackbar.LENGTH_LONG)
    snackbar.view.setBackgroundColor(color)
    snackbar.setTextColor(Color.WHITE)
    snackbar.show()
}

fun String.showToast(context: Context) {

    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun MutableIterable<DataSnapshot>.getImageList(): ArrayList<ImageData> {
    val imageList = arrayListOf<ImageData>()
    for (item in this) {
        val imageData = item.getValue(ImageData::class.java)
        imageList.add(imageData!!)
    }
    return imageList

}

fun TextView.showListStatus(isEmpty: Boolean) {
    if (isEmpty) {
        this.text = "No Item Available"
    } else {
        this.text = "Uploaded Items"
    }
}

fun Fragment.waitForTransition(targetView: View) {
    postponeEnterTransition()
    targetView.doOnPreDraw { startPostponedEnterTransition() }
}

fun View.toTransitionGroup() = this to transitionName