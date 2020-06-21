package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.Gate
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
class FBPlaceRepository : BaseFB() {
  /**
   * TEST 함수
   * 장소 셋팅하는 함수
   */
  fun setPlace(placeInfoData: PlaceData) {
    db.collection(PLACE).document(placeInfoData.id.toString()).set(placeInfoData)
  }

  /**
   * 장소들 목록을 불러오는 함수
   */
  fun listPlaces(listener: (ArrayList<PlaceData>) -> Unit) {
    db.collection(PLACE).get()
      .addOnSuccessListener { snapshot ->
        listener(ArrayList(snapshot.map { it.toObject(PlaceData::class.java) }))
      }
  }

  suspend fun getPlaceInfo(id: String): PlaceData {
    return db.collection(PLACE).whereEqualTo(ID, id)
      .get()
      .await()
      .documents.first().toObject(PlaceData::class.java)!!
  }

  fun getPlace(placeRef: DocumentReference): PlaceData? {
    return null
  }

  suspend fun getGateIp(gateId: String): String {
    return db.collection(GATES).whereEqualTo(GATE_ID, gateId).get().await().first()
      .getString(GATE_IP)!!
  }

  suspend fun listGates(ticketRef: DocumentReference): List<Gate> {
    return ticketRef.get().await().toObject(TicketData::class.java)!!.placeRef!!.collection(GATES)
      .get().await().toObjects(Gate::class.java)
  }
}