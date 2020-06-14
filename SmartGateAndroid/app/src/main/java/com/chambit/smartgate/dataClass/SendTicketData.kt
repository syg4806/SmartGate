package com.chambit.smartgate.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SendTicketData (
  var ticketId: String? = null,
  var certificateNo: Long? = null,
  var expirationDate: Long?=null
)  : Parcelable