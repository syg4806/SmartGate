package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.MyTicketData
import com.chambit.smartgate.dataClass.TicketData
import com.google.firebase.firestore.DocumentReference


/**
 * 받아온 티켓 돌려주는 리스너
 */
interface GetTicketListener {
  /**
   * 받아온 티켓들
   */
  fun tickets(ticketDatas: ArrayList<TicketData>)

  /**
   * 받아온 내 티켓들
   */
  fun myTickets(myTicketDatas: ArrayList<MyTicketData>, ticketDatas: ArrayList<TicketData>)

  /**
   * 티켓을 구매했을 때, 마이 티켓에
   * 레퍼런스를 등록할 수 있도록 반환
   */
  fun getTicketReference(reference: DocumentReference)
}