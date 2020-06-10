package com.chambit.smartgate.ui.send

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chambit.smartgate.R
import com.chambit.smartgate.util.Logg
import com.kakao.friends.AppFriendContext
import com.kakao.friends.AppFriendOrder
import com.kakao.friends.response.AppFriendsResponse
import com.kakao.friends.response.model.AppFriendInfo
import com.kakao.kakaotalk.callback.TalkResponseCallback
import com.kakao.kakaotalk.v2.KakaoTalkService
import com.kakao.network.ErrorResult
import kotlinx.android.synthetic.main.activity_send_ticket.*


class SendTicketActivity : AppCompatActivity() {
  val activity = this
  val friendList = ArrayList<AppFriendInfo>()
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_send_ticket)

    // 컨텍스트 생성
//   - 닉네임, 처음(index 0)부터, 100명까지, 오름차순 예시
    val context = AppFriendContext(AppFriendOrder.NICKNAME, 0, 100, "asc")

    // 조회 요청
    KakaoTalkService.getInstance().requestAppFriends(context, object : TalkResponseCallback<AppFriendsResponse>(){
      override fun onNotKakaoTalkUser() {
        Logg.e( "카카오톡 사용자가 아님");
      }

      override fun onSessionClosed(errorResult: ErrorResult?) {
        Logg.e( "세션이 닫혀 있음: " + errorResult);
      }

      override fun onFailure(errorResult: ErrorResult?) {
        super.onFailure(errorResult)
        Logg.e( "친구 조회 실패: " + errorResult);
      }

      override fun onSuccess(result: AppFriendsResponse?) {
        Logg.i( "친구 조회 성공")

        for (friend in result!!.friends) {
          Logg.d( friend.toString())
          friendList.add(friend)
          val uuid = friend.uuid // 메시지 전송 시 사용
        }

        SelectFriendRecyclerView.layoutManager = LinearLayoutManager(activity)
        SelectFriendRecyclerView.adapter = FriendListRecyclerViewAdapter(friendList, activity)
      }
    } )
  }
}
