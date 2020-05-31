package com.chambit.smartgate.network

import com.chambit.smartgate.util.SharedPref

class FBPaymentKeyRepository: BaseFB() {

  fun setPaymentKey(key: String) {
    //db.collection("users").whereEqualTo("uid", SharedPref.autoLoginKey).add
  }
}