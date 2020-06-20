package com.chambit.smartgate.dataClass

import com.kakao.friends.response.model.AppFriendInfo

data class KakaoFriendInfo(
  var friendInfo: AppFriendInfo? = null,
  var selectFlag: Boolean = false
)