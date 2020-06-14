package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.*
import com.chambit.smartgate.extension.show
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.SharedPref
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FBTicketRepository {
  val db = FirebaseFirestore.getInstance()

  /**
   * 장소와 티켓데이터를 통해
   * 장소 안에 컬렉션으로 티켓을 반영한다.
   */
  fun setTicket(ticketData: TicketData, setDataListener: SetDataListener) {
    db.collection("place").whereEqualTo("id", "1588783928489")
      .get()
      .addOnSuccessListener {
        ticketData.placeRef = it.documents.last().reference
        ticketData.placeRef!!.collection("tickets").document(ticketData.id.toString())
          .set(ticketData)
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

  // TODO : 컬렉션 이름이 사용안하는 DB 같은데 수정 좀
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
  fun buyTicket(
    ticketRef: DocumentReference,
    expirationDate: Long,
    selectedDateFrom: Long,
    ticketCount: Int
  ) {
    for (i in 0 until ticketCount) { // 티켓 수 만큼 반복 돌면서 set
      db.collection("users").whereEqualTo("uid", SharedPref.autoLoginKey).get()
        .addOnSuccessListener {
          //TODO 선물 상태 추가
          val ownedTicket =
            OwnedTicket(
              System.currentTimeMillis(),
              ticketRef,
              false,
              selectedDateFrom,
              TicketGiftState.NO_GIFT_YET,
              expirationDate
            )
          it.last().reference.collection("ownedTickets")
            .document(ownedTicket.certificateNo.toString()).set(ownedTicket)
        }
    }
  }

  /**
   * user가 보유한 ownedTickets의 전체 리스트를 반환한다.
   */
  suspend fun listOwnedTickets(used: Boolean): MutableList<OwnedTicket> {
    Logg.d("ListTickets start")
    val data = db.collection("users").document(SharedPref.autoLoginKey)
      .collection("ownedTickets")
      .whereEqualTo("used", used)
      .get()
      .await()
    Logg.d("await finish")
    return data!!.toObjects(OwnedTicket::class.java)
  }

  /**
   * ticketRef에 해당하는 TicketData를 반환한다.
   */
  suspend fun getTicket(ticketRef: DocumentReference): TicketData {
    return ticketRef.get().await().toObject(TicketData::class.java)!!
  }

  suspend fun getToDayPurchaseTicketList(purchaseDay: Long) : Array<SendTicketData> {
    Logg.d("${purchaseDay}")
    return db.collection("users").document(SharedPref.autoLoginKey)
      .collection("ownedTickets").whereEqualTo("dateOfPurchase", purchaseDay)
      .get()
      .await()
      .documents.map {
      val ticketRef = it.get("ticketRef") as DocumentReference
      val certificateNo = it.get("certificateNo") as Long
      val expirationDate = it.get("expirationDate") as Long
      SendTicketData(ticketRef.id, certificateNo, expirationDate)
    }.toTypedArray()
  }

  suspend fun deleteTicket(certificateNo: Long): Boolean {
    return try {
      db.collection("users").document(SharedPref.autoLoginKey).collection("ownedTickets")
        .whereEqualTo("certificateNo", certificateNo).get()
        .await().documents.first().reference.delete().await()
      "티켓이 사용되었습니다.".show()
      true
    } catch (e: Exception) {
      "티켓 사용에 실패 했습니다.".show()
      false
    }
  }

  suspend fun getOwnedTicket(certificateNo: Long?): OwnedTicket? {
    Logg.d("${SharedPref.autoLoginKey} $certificateNo")
    return db.collection("users").document(SharedPref.autoLoginKey)
      .collection("ownedTickets").whereEqualTo("certificateNo", certificateNo)
      .get()
      .await().documents.first().toObject(OwnedTicket::class.java)
  }
}