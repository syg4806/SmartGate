package com.chambit.smartgate.network

import android.os.Message
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chambit.smartgate.App
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class FBTicketImage {
    val storage = FirebaseStorage.getInstance("gs://smartgate-60162.appspot.com/")

    fun getTicketImage(imageView: ImageView, ticketId: String) {
        val mapImageRef = storage.reference.child("tickets").child(ticketId)
        mapImageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                Glide.with(App.instance.context())
                    .load(task.result)
                    .override(1024, 980)
                    .into(imageView)
            }
        }
    }
}