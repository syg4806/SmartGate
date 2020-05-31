package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.UserInfo
import com.chambit.smartgate.util.SharedPref

class FBUsersRepository : BaseFB() {
  fun userSignUp(email: String) {
    val userInfo = UserInfo(SharedPref.autoLoginKey, email, SharedPref.paymentKey)
    db.collection("users").document(userInfo.uid!!).set(userInfo)
  }
}