package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.PlaceData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.network.BaseFB.Companion.GATES
import com.chambit.smartgate.network.BaseFB.Companion.GATE_ID
import com.chambit.smartgate.network.BaseFB.Companion.GATE_IP
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

  fun listPlaces(listener: (ArrayList<PlaceData>) -> Unit) {
    db.collection("place").get()
      .addOnSuccessListener { snapshot ->
        listener(ArrayList(snapshot.map { it.toObject(PlaceData::class.java) }))
      }
  }

  fun getPlaceInfo(id: String, listener: (PlaceData) -> Unit) {
    db.collection("place").whereEqualTo("id", id)
      .get()
      .addOnSuccessListener {
        listener(it.documents.last().toObject(PlaceData::class.java)!!)
      }
  }

  fun getPlace(placeRef: DocumentReference): PlaceData? {
    return null
  }

  suspend fun listAvailableTickets(beaconId: String): MutableList<TicketData>? {
    Logg.d("find place by beacon ID : ${beaconId}")
    return db.collection("place").whereArrayContains("gateArray", beaconId)
      .get().await().documents.first().reference.collection("tickets").get().await()
      ?.toObjects(TicketData::class.java)
  }

  suspend fun getGateIp(gateId: String): String {
    val a = db.collection(GATES).whereEqualTo(GATE_ID, gateId).get().await()
    return a.first().getString(GATE_IP)!!
  }

  suspend fun listGates(ticketRef: DocumentReference): List<String> {
    return ticketRef.get().await().toObject(TicketData::class.java)!!.placeRef!!.get()
      .await().toObject(PlaceData::class.java)?.gateArray!!
  }
}