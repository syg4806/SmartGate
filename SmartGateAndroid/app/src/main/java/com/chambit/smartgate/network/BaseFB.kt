package com.chambit.smartgate.network

import android.net.Uri
import com.chambit.smartgate.App
import com.chambit.smartgate.util.MyProgressBar
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class BaseFB {
  private val storage = FirebaseStorage.getInstance("gs://smartgate-60162.appspot.com/")

  suspend fun getImage(imgPath: String): Uri {
    return storage.reference.child(imgPath).downloadUrl.await()
  }
}
