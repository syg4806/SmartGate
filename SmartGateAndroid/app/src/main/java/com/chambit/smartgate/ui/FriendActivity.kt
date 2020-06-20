package com.chambit.smartgate.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.util.Logg
import com.kakao.friends.AppFriendContext
import com.kakao.friends.AppFriendOrder
import com.kakao.friends.response.AppFriendsResponse
import com.kakao.kakaotalk.callback.TalkResponseCallback
import com.kakao.kakaotalk.v2.KakaoTalkService
import com.kakao.network.ErrorResult


class FriendActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_friend)

    val context = AppFriendContext(AppFriendOrder.NICKNAME, 0, 100, "asc")

    KakaoTalkService.getInstance()
      .requestAppFriends(context, object : TalkResponseCallback<AppFriendsResponse>() {
        override fun onNotKakaoTalkUser() {
          Logg.e("카카오톡 사용자가 아님");
        }

        override fun onSessionClosed(errorResult: ErrorResult?) {
          Logg.e("세션이 닫혀 있음: $errorResult")
        }

        override fun onFailure(errorResult: ErrorResult?) {
          super.onFailure(errorResult)
          Logg.e("친구 조회 실패: $errorResult")
        }

        override fun onSuccess(result: AppFriendsResponse?) {
          Logg.e("친구 조회 성공")
          Logg.e(result!!.totalCount.toString())
          Logg.e(result.friends.size.toString())
          for (friend in result.friends) {
            Logg.e(friend.toString())
            //val uuid = friend.uuid // 메시지 전송 시 사용
          }
        }
      })

  }
}
