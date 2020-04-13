package com.chambit.smartgate.network

import android.widget.Toast
import com.chambit.smartgate.dataClass.PlaceInfoData
import com.chambit.smartgate.dataClass.PlaceListData
import com.chambit.smartgate.dataClass.TicketData
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

  fun getPlaceList(listener: GetPlaceListener) {
    val placeListDatas = arrayListOf<PlaceListData>()
    db.collection("place").get()
      .addOnSuccessListener {
        it.documents.forEach { document ->
          val placeListData = PlaceListData(document.get("placeLogoPath") as String, document.get("placeName") as String)
          placeListDatas.add(placeListData)
        }
        listener.getPlaceList(placeListDatas)
      }
  }
}