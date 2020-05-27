package com.chambit.smartgate.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfo(
  var uid: String? = null, //
  var phoneNumber: String? = null //
) : Parcelable

