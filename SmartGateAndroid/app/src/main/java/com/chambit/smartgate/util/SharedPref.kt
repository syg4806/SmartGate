package com.chambit.smartgate.util

import com.chibatching.kotpref.KotprefModel

object SharedPref : KotprefModel() {
  var autoLoginKey by stringPref() // 자동 로그인 유무 판단을 위해 firebase의 mAuth!!.currentUser 저장
}