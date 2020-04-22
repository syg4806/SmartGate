package com.chambit.smartgate.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
/**
 *  모든 장소에 대한 로고, 이름 데이터
 */
data class PlaceInfoData(
  var placeName: String? = null, // 장소 이름
  var discription: String? = null, // 장소 설명
  var placeLogoPath: String? = null, // 장소 로고 이미지 경로
  var placeImagePath: String? = null // 장소 이미지 경로

  // 장소 설명
  // 장소 시간 정보 (오픈, 휴일...)
) : Parcelable
/*

 */
