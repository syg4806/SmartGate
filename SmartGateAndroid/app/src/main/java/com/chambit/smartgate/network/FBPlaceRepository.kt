package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.PlaceData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.util.Logg
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * 장소 데이터 관련한 Repository
 */
class FBPlaceRepository {
  val db = FirebaseFirestore.getInstance()

  /**
   * 장소 셋팅하는 함수
   */
  fun setPlace(placeInfoData: PlaceData, listener: SetDataListener) {
    db.collection("place").document(placeInfoData.id.toString()).set(placeInfoData)
      .addOnSuccessListener {
        listener.setPlaceData()
      }
  }

  fun listPlaces(listener: (MutableList<PlaceData>) -> Unit) {
    db.collection("place").get()
      .addOnSuccessListener { snapshot ->
        listener(ArrayList(snapshot.map { it.toObject(PlaceData::class.java) }))
      }
  }

  suspend fun getPlaceInfo(id: String): PlaceData {
    return db.collection("place").whereEqualTo("id", id)
      .get()
      .await()
      .documents.first().toObject(PlaceData::class.java)!!

  }

  suspend fun getPlace(placeRef: DocumentReference): PlaceData? {
    return placeRef.get().await().toObject(PlaceData::class.java)
  }

  suspend fun listAvailableTickets(beaconId: String): MutableList<TicketData>? {
    Logg.d("find place by beacon ID : ${beaconId}")
    return db.collection("place").whereArrayContains("gateArray", beaconId)
      .get().await().documents.first().reference.collection("tickets").get().await()
      ?.toObjects(TicketData::class.java)
  }

  suspend fun searchPlace(keyword: String): MutableList<PlaceData>? {
    val documentSnapshot = db.collection("place").whereArrayContains("tag", keyword).get().await()
    if (documentSnapshot.isEmpty)
      return null
    else
      return documentSnapshot.documents.map {
        it.toObject(PlaceData::class.java)!!
      }.toMutableList()

  }
}