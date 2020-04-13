package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.PlaceInfoData
import com.chambit.smartgate.dataClass.TicketData


/**
 * 받아온 티켓 돌려주는 리스너
 */
interface SetDataListener {
  /**
   * 받아온 티켓들
   */
  fun setPlaceData()

  /**
   * 받아온 내 티켓들
   */
  fun setTicketData()
}