package com.chambit.smartgate.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfo(
  var uid: String? = null, // 카카오 uid
  var email: String? = null, // 카카오 email
  var paymentKey: String? = null // 개인 결제 비밀번호
) : Parcelable

