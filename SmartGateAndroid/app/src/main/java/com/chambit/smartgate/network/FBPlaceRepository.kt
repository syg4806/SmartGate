package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.PlaceData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * 장소 데이터 관련한 Repository
 */
class FBPlaceRepository {
  val db = FirebaseFirestore.getInstance()

  /**
   * 장소 셋팅하는 함수
   */
  fun setPlace(placeInfoData: PlaceData, listener: SetDataListener) {
    db.collection("place").add(placeInfoData)
      .addOnSuccessListener {
        listener.setPlaceData()
      }
  }

  fun listPlaces(listener: (ArrayList<PlaceData>)->Unit) {
    db.collection("place").get()
      .addOnSuccessListener {snapshot->
        listener(ArrayList(snapshot.map{it.toObject(PlaceData::class.java)}))
      }
  }

  fun getPlaceInfo(id: String, listener: (PlaceData)->Unit) {
    db.collection("place").whereEqualTo("id", id)
      .get()
      .addOnSuccessListener {
        listener( it.documents.last().toObject(PlaceData::class.java)!!)
      }
  }

  fun getPlace(placeRef: DocumentReference):PlaceData? {
    return null
  }
}