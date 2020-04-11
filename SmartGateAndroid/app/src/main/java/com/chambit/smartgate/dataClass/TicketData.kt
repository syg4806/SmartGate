package com.chambit.smartgate.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TicketData (
    var ticketId: String? = null, // 티켓 id
    var ticketPlace: String? = null, // 티켓 장소
    var ticketKinds: String? = null, // 티켓 종류
    var ticketDate: String? = null, // 티켓 날짜
    var ticketCount: String? = null, // 티켓 개수
    var ticketImagePath: String? = null // 티켓 이미지 경로
) : Parcelable
