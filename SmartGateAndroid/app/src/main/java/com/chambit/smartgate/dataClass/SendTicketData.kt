package com.chambit.smartgate.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


data class SendTicketData (
  var ticketId: String? = null,
  var certificateNo: Long? = null,
  var expirationDate: Long?=null
)  : Serializable