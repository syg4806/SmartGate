package com.chambit.smartgate.network

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.MyProgressBar
import com.google.firebase.storage.FirebaseStorage

class FBPlaceImageRepository() {
  private val storage = FirebaseStorage.getInstance("gs://smartgate-60162.appspot.com/")

  fun getPlaceLogoImage(imageView: ImageView, logoPath: String, activity: Activity) {
    val progressBar = MyProgressBar(activity)
    progressBar.show()

    val mapImageRef = storage.reference.child(logoPath)
    mapImageRef.downloadUrl.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Glide 이용하여 이미지뷰에 로딩
        Glide.with(activity)
          .load(task.result)
          .override(1024, 980)
          .into(imageView)

        progressBar.dismiss()
      }
    }
  }

  fun getPlaceImage(imageView: ImageView, ImagePath: String, activity: Activity) {
    val progressBar = MyProgressBar(activity)
    progressBar.show()

    val mapImageRef = storage.reference.child(ImagePath)
    mapImageRef.downloadUrl.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Glide 이용하여 이미지뷰에 로딩
        Glide.with(activity)
          .load(task.result)
          .override(1024, 980)
          .into(imageView)

        progressBar.dismiss()
      }
    }
  }


}