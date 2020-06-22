package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.*
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.SharedPref
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.tasks.await

class FBTicketRepository : BaseFB() {

  /**
   * TEST 함수
   * 장소와 티켓데이터를 통해
   * 장소 안에 컬렉션으로 티켓을 반영한다.
   */
  suspend fun setTicket(ticketData: TicketData): Boolean {
    return placeRef.whereEqualTo(ID, "1588783928489")
      .get()
      .await()
      .documents.first().reference.collection(TICKETS).document(ticketData.id.toString())
      .set(ticketData).isSuccessful
  }

  /**
   * 장소를 넘겨주면 장소에 있는
   * 모든 티켓들을 가져온다.
   */
  suspend fun getTickets(name: String): MutableList<TicketData> {
    return placeRef.whereEqualTo(NAME, name)
      .get().await().documents.last().reference.collection(TICKETS)
      .get().await().documents.map {
        it.toObject(TicketData::class.java)!!
      }.toMutableList()
  }


  /**
   * ticketRef의 티켓을 expirationDate기한으로 ticketCount개 구매해서 유저 ownedTickets에 set한다
   */
  fun buyTicket(
    ticketRef: DocumentReference,
    expirationDate: Long, // 만기일
    selectedDateFrom: Long, // 구매일
    ticketCount: Int,
    giftState: TicketGiftState
  ) {
    for (i in 0 until ticketCount) {
      userRef.whereEqualTo(UID, SharedPref.autoLoginKey).get()
        .addOnSuccessListener {
          val ownedTicket =
            OwnedTicket(
              System.currentTimeMillis(),
              ticketRef,
              TicketState.UNUSED,
              selectedDateFrom,
              giftState,
              expirationDate
            )
          it.last().reference.collection(OWNED_TICKET)
            .document(ownedTicket.certificateNo.toString()).set(ownedTicket)
        }

    }
  }
  /*
   val ownedTicket =
            OwnedTicket(
              System.currentTimeMillis(),
              ticketRef,
              false,
              selectedDateFrom,
              giftState,
              expirationDate
            )
   */

  /**
   * user가 보유한 ownedTickets의 전체 리스트를 반환한다.
   */
  suspend fun listOwnedTickets(ticketState: TicketState): MutableList<OwnedTicket> {
    return userRef.document(SharedPref.autoLoginKey)
      .collection(OWNED_TICKET)
      .whereEqualTo(USED, ticketState)
      .get()
      .await().toObjects(OwnedTicket::class.java)
  }

  /**
   * ticketRef에 해당하는 TicketData를 반환한다.
   */
  suspend fun getTicket(ticketRef: DocumentReference): TicketData {
    return ticketRef.get().await().toObject(TicketData::class.java)!!
  }

  /**
   * 예약하기 하고 바로 선물하기 할때 구매 시간을 기반으로 정보 얻어오기
   */
  suspend fun getToDayPurchaseTicketList(purchaseDay: Long): Array<SendTicketData> {
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

  /**
   *  유저 ID 값을 통해 선물 상태 변경
   */
  fun changeGiftState(userID: String, certificateNo: String, giftState : TicketGiftState) {
    db.collection("users").document(userID).collection(OWNED_TICKET).document(certificateNo)
      .update("giftState", giftState)

  }

  suspend fun useTicket(certificateNo: Long): Boolean {
    return try {
      userRef.document(SharedPref.autoLoginKey).collection(OWNED_TICKET)
        .whereEqualTo(CERTIFICATE_NO, certificateNo).get()
        .await().documents.first().reference.update(
          USED, TicketState.USED
        ).await()
      true
    } catch (e: Exception) {
      false
    }
  }

  suspend fun getOwnedTicket(certificateNo: Long?): OwnedTicket? {
    return userRef.document(SharedPref.autoLoginKey)
      .collection(OWNED_TICKET).whereEqualTo(CERTIFICATE_NO, certificateNo)
      .get()
      .await().documents.first().toObject(OwnedTicket::class.java)
  }
}
