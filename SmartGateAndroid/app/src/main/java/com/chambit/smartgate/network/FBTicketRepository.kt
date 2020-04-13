package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.MyTicketData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.util.SharedPref
import com.google.firebase.firestore.FirebaseFirestore

class FBTicketRepository {
  val db = FirebaseFirestore.getInstance()

  /**
   * 장소와 티켓데이터를 통해
   * 장소 안에 컬렉션으로 티켓을 반영한다.
   */
  fun setTicket(ticketData: TicketData, setDataListener: SetDataListener) {
    db.collection("place").whereEqualTo("placeName", ticketData.ticketPlace)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection("tickets").add(ticketData)
        setDataListener.setTicketData()
      }
  }

  /**
   * 장소를 넘겨주면 장소에 있는
   * 모든 티켓들을 가져온다.
   */
  fun getTickets(place: String) {
    val ticketDatas = arrayListOf<TicketData>()
    db.collection("place").whereEqualTo("placeName", place)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection("tickets")
          .get()
          .addOnSuccessListener { result ->
            result.forEach { document ->
              val ticketData = document.toObject(TicketData::class.java)
              ticketDatas.add(ticketData)
            }
          }
      }
  }

  /**
   *
   */
  fun getMyTickets(listener: GetTicketListener) {
    db.collection("userinfo").document(SharedPref.autoLoginKey)
      .collection("tickets")
      .get()
      .addOnSuccessListener { result ->
        val myTicketDatas = arrayListOf<MyTicketData>()
        for (document in result) {
          val myTicketData = document.toObject(MyTicketData::class.java)
          myTicketDatas.add(myTicketData)
        }
        listener.myTickets(myTicketDatas)
      }
  }
}