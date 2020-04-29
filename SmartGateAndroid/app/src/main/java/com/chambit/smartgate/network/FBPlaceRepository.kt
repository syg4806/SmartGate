package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.PlaceInfoData
import com.google.firebase.firestore.FirebaseFirestore

/**
 * 장소 데이터 관련한 Repository
 */
class FBPlaceRepository {
  val db = FirebaseFirestore.getInstance()

  /**
   * 장소 셋팅하는 함수
   */
  fun setPlace(placeInfoData: PlaceInfoData, listener: SetDataListener) {
    db.collection("place").add(placeInfoData)
      .addOnSuccessListener {
        listener.setPlaceData()
      }
  }

  fun listPlaces(listener: (ArrayList<PlaceInfoData>)->Unit) {
    db.collection("place").get()
      .addOnSuccessListener {snapshot->
        listener(ArrayList(snapshot.map{it.toObject(PlaceInfoData::class.java)}))
      }
  }

  fun getPlaceInfo(id: String, listener: (PlaceInfoData)->Unit) {
    db.collection("place").whereEqualTo("id", id)
      .get()
      .addOnSuccessListener {
        listener( it.documents.last().toObject(PlaceInfoData::class.java)!!)
      }
  }
}