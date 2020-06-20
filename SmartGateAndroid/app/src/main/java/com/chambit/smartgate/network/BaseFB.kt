package com.chambit.smartgate.network

import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class BaseFB {
  private val storage = FirebaseStorage.getInstance("gs://smartgate-60162.appspot.com/")
  val db = FirebaseFirestore.getInstance()


  suspend fun getImage(imgPath: String): Uri {
    return storage.reference.child(imgPath).downloadUrl.await()
  }

  fun getTicketRef(placeId: String, ticketId: String): DocumentReference {
    return db.collection("place").document(placeId).collection("tickets").document(ticketId)
  }
}
