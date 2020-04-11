package com.chambit.smartgate.network

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chambit.smartgate.App
import com.chambit.smartgate.util.Logg
import com.google.firebase.storage.FirebaseStorage

class FBTicketImage {
    val storage = FirebaseStorage.getInstance("gs://smartgate-60162.appspot.com/")

    // TODO: 보안규칙 이슈가 있는듯 firebase 서버 정상화 되면 작업
    fun getTicketImage(imageView: ImageView, ticketId: String, activity: Activity) {
        val mapImageRef = storage.reference.child("tickets").child("$ticketId.jpg")
        mapImageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                Glide.with(activity)
                    .load(task.result)
                    .override(1024, 980)
                    .into(imageView)
            }
        }
    }
}