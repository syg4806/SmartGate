package com.chambit.smartgate.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyTicketData(
  var ticketId: String? = null, // 티켓 id
  var ticketPath: String? = null, // db 에 저장되어있는 실제 티켓 경로
  var ticketCount: String? = null, // 티켓 개수
  var isused: Boolean? = null // 티켓이 사용되었는지 여부
) : Parcelable

