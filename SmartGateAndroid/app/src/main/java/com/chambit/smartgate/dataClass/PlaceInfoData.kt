package com.chambit.smartgate.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
/**
 *  모든 장소에 대한 로고, 이름 데이터
 */
data class PlaceInfoData(
    // 장소 id (key)
    var placeLogo: String? = null, // 이미지
    var placeName: String? = null // String
    // 장소 설명
    // 장소 시간 정보 (오픈, 휴일...)
) : Parcelable