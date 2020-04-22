package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.MyTicketData
import com.chambit.smartgate.dataClass.TicketData
import com.google.firebase.firestore.DocumentReference


/**
 * 티켓 업로드를 알려주는 리스너
 */
interface SetTicketListener {

  /**
   * 내 티켓 업로드 했다고 알려주는 함수
   */
  fun setMyTicket()
}