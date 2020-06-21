package com.chambit.smartgate.network

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.security.cert.Certificate

open class BaseFB {
  val storage = FirebaseStorage.getInstance("gs://smartgate-60162.appspot.com/")
  val db = FirebaseFirestore.getInstance()
  val userRef = db.collection(USERS)
  val placeRef = db.collection(PLACE)

  companion object {
    const val GATES = "gates"
    const val GATE_ID = "gateId"
    const val GATE_IP = "gateIP"
    const val GATE_ARRAY = "gateArray"

    const val PLACE = "place"

    const val TICKETS = "tickets"
    const val OWNED_TICKET = "ownedTickets"

    const val ID = "id"
    const val UID = "uid"
    const val NAME = "name"

    const val USERS = "users"

    const val USED = "used"

    const val CERTIFICATE_NO = "certificateNo"
    const val CARD = "card"
  }

  suspend fun getImage(imgPath: String): Uri {
    return storage.reference.child(imgPath).downloadUrl.await()
  }
}
