package com.chambit.smartgate.network

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

open class BaseFB {
  private val storage = FirebaseStorage.getInstance("gs://smartgate-60162.appspot.com/")
  val db = FirebaseFirestore.getInstance()


  suspend fun getImage(imgPath: String): Uri {
    return storage.reference.child(imgPath).downloadUrl.await()
  }
}
