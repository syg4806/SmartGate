package com.chambit.smartgate.network


/**
 * 티켓 업로드를 알려주는 리스너
 */
interface SetTicketListener {

  /**
   * 내 티켓 업로드 했다고 알려주는 함수
   */
  fun setMyTicket()
}