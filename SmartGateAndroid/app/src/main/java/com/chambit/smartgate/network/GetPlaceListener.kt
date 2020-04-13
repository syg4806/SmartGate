package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.MyTicketData
import com.chambit.smartgate.dataClass.PlaceInfoData
import com.chambit.smartgate.dataClass.PlaceListData
import com.chambit.smartgate.dataClass.TicketData


/**
 * 받아온 장소 돌려주는 리스너
 */
interface GetPlaceListener {
  /**
   * 받아온 장소 리스트들
   */
  fun getPlaceList(placeListDatas: ArrayList<PlaceListData>)

  /**
   * 받아온 장소 정보
   */
  fun getPlaceInfo(placeInfoData: PlaceInfoData)
}