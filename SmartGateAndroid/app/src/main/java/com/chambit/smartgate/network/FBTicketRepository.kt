package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.MyTicketData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.SharedPref
import com.google.firebase.auth.UserInfo
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class FBTicketRepository {
  val db = FirebaseFirestore.getInstance()

  /**
   * 장소와 티켓데이터를 통해
   * 장소 안에 컬렉션으로 티켓을 반영한다.
   */
  fun setTicket(ticketData: TicketData, setDataListener: SetDataListener) {
    db.collection("place").whereEqualTo("id", "1588764861486")
      .get()
      .addOnSuccessListener {
        ticketData.placeRef = it.documents.last().reference
        ticketData.placeRef!!.collection("tickets").add(ticketData)
        setDataListener.setTicketData()
      }
  }

  /**
   * 장소를 넘겨주면 장소에 있는
   * 모든 티켓들을 가져온다.
   */
  fun getTickets(name: String, getTicketListener: GetTicketListener) {
    val ticketDatas = arrayListOf<TicketData>()
    db.collection("place").whereEqualTo("name", name)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection("tickets")
          .get()
          .addOnSuccessListener { result ->
            result.forEach { document ->
              val ticketData = document.toObject(TicketData::class.java)
              ticketDatas.add(ticketData)
            }
            getTicketListener.tickets(ticketDatas)
          }
      }
  }

  fun setMyTicket(myTicketData: MyTicketData, setTicketListener: SetTicketListener) {
    db.collection("userinfo").whereEqualTo("uid", SharedPref.autoLoginKey)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection("tickets")
          .add(myTicketData)
        setTicketListener.setMyTicket()
      }

  }

  fun getTicket(name: String, id: String, getTicketListener: GetTicketListener) {
    db.collection("place").whereEqualTo("name", name)
      .get()
      .addOnSuccessListener {
        it.documents.last().reference.collection("tickets")
          .whereEqualTo("id", id)
          .get()
          .addOnSuccessListener {
            getTicketListener.getTicketReference(it.documents.last().reference)
          }
      }
  }

  /**
   *
   */
  fun getMyTickets(listener: GetTicketListener) {
    val myTicketDatas = arrayListOf<MyTicketData>()
    val ticketDatas = arrayListOf<TicketData>()
    var count = 0
    db.collection("userinfo").document(SharedPref.autoLoginKey)
      .collection("tickets")
      .get()
      .addOnSuccessListener { result ->
        if (result.isEmpty) {
          listener.myTickets(myTicketDatas, ticketDatas)
        }
        for (document in result) {

          val myTicketData = document.toObject(MyTicketData::class.java)
          myTicketDatas.add(myTicketData)

          myTicketData.ticketRef!!.get()
            .addOnSuccessListener {
              val ticketData = it.toObject(TicketData::class.java)
              ticketDatas.add(ticketData!!)
              count++
              if (count == result.size()) {
                listener.myTickets(myTicketDatas, ticketDatas)
              }
            }
        }
      }
  }
}