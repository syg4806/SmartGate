package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.MyTicketData
import com.chambit.smartgate.dataClass.OwnedTicket
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.SharedPref
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class FBTicketRepository {
  val db = FirebaseFirestore.getInstance()

  /**
   * 장소와 티켓데이터를 통해
   * 장소 안에 컬렉션으로 티켓을 반영한다.
   */
  fun setTicket(ticketData: TicketData, setDataListener: SetDataListener) {
    db.collection("place").whereEqualTo("id", "1588159202345")
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

  /**
   * ticketRef의 티켓을 expirationDate기한으로 ticketCount개 구매해서 유저 ownedTickets에 set한다
   */
  fun buyTicket(ticketRef: DocumentReference, expirationDate: Long, ticketCount: Int) {
    for (i in 0 until ticketCount) {
      db.collection("users").whereEqualTo("uid", SharedPref.autoLoginKey).get()
        .addOnSuccessListener {
          val ownedTicket =
            OwnedTicket(System.currentTimeMillis(), ticketRef, false, expirationDate)
          it.last().reference.collection("ownedTickets")
            .document(ownedTicket.certificateNo.toString()).set(ownedTicket)
        }
    }
  }

  /**
   * user가 보유한 ownedTickets의 리스트를 반환한다.
   */
  suspend fun listOwnedTickets():MutableList<OwnedTicket>{
    Logg.d("ListTickets start")
    val data = db.collection("users").document(SharedPref.autoLoginKey)
      .collection("ownedTickets")
      .get()
      .await()
    Logg.d("await finish")
    return data!!.toObjects(OwnedTicket::class.java)
  }

  /**
   * ticketRef에 해당하는 TicketData를 반환한다.
   */
  suspend fun getTicket(ticketRef: DocumentReference) : TicketData{
     return ticketRef.get().await().toObject(TicketData::class.java)!!
  }
}