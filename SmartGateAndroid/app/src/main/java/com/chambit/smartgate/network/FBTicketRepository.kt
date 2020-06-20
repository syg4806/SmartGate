package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.OwnedTicket
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.dataClass.TicketState
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.SharedPref
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.tasks.await

class FBTicketRepository : BaseFB() {

  /**
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
  fun buyTicket(ticketRef: DocumentReference, expirationDate: Long, ticketCount: Int) {
    for (i in 0 until ticketCount) {
      userRef.whereEqualTo(UID, SharedPref.autoLoginKey).get()
        .addOnSuccessListener {
          val ownedTicket =
            OwnedTicket(System.currentTimeMillis(), ticketRef, TicketState.UNUSED, expirationDate)
          it.last().reference.collection(OWNED_TICKET)
            .document(ownedTicket.certificateNo.toString()).set(ownedTicket)
        }
    }
  }

  /**
   * user가 보유한 ownedTickets의 리스트를 반환한다.
   */
  suspend fun listOwnedTickets(ticketState: TicketState): MutableList<OwnedTicket> {
    val data = userRef.document(SharedPref.autoLoginKey)
      .collection(OWNED_TICKET)
      .whereEqualTo(USED, ticketState)
      .get()
      .await()
    return data!!.toObjects(OwnedTicket::class.java)
  }

  /**
   * ticketRef에 해당하는 TicketData를 반환한다.
   */
  suspend fun getTicket(ticketRef: DocumentReference): TicketData {
    return ticketRef.get().await().toObject(TicketData::class.java)!!
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