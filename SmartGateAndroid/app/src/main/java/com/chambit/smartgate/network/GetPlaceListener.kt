package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.PlaceData


/**
 * 받아온 장소 돌려주는 리스너
 */
interface GetPlaceListener {
  /**
   * 받아온 장소 리스트들
   */
  fun getPlaceList(placeList: ArrayList<PlaceData>)

  /**
   * 받아온 장소 정보
   */
  fun getPlaceInfo(placeInfoData: PlaceData)
}