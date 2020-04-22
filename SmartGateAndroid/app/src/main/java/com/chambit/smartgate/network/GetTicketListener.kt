package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.MyTicketData
import com.chambit.smartgate.dataClass.TicketData


/**
 * 받아온 티켓 돌려주는 리스너
 */
interface GetTicketListener {
  /**
   * 받아온 티켓들
   */
  fun tickets(ticketData: ArrayList<TicketData>)

  /**
   * 받아온 내 티켓들
   */
  fun myTickets(myTicketData: ArrayList<MyTicketData>)
}