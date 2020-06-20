package com.chambit.smartgate.ui.send

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.KakaoFriendInfo
import com.chambit.smartgate.util.Logg
import com.kakao.friends.AppFriendContext
import com.kakao.friends.AppFriendOrder
import com.kakao.friends.response.AppFriendsResponse
import com.kakao.kakaotalk.callback.TalkResponseCallback
import com.kakao.kakaotalk.response.MessageSendResponse
import com.kakao.kakaotalk.v2.KakaoTalkService
import com.kakao.message.template.LinkObject
import com.kakao.message.template.TemplateParams
import com.kakao.message.template.TextTemplate
import com.kakao.network.ErrorResult
import kotlinx.android.synthetic.main.activity_send_ticket.*


class SendTicketActivity : AppCompatActivity() {
  val activity = this
  val friendList = ArrayList<KakaoFriendInfo>()
  val uuids = ArrayList<String>()
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_send_ticket)

    // 컨텍스트 생성
//   - 닉네임, 처음(index 0)부터, 100명까지, 오름차순 예시
    val context = AppFriendContext(AppFriendOrder.NICKNAME, 0, 100, "asc")

    // 조회 요청
    KakaoTalkService.getInstance()
      .requestAppFriends(context, object : TalkResponseCallback<AppFriendsResponse>() {
        override fun onNotKakaoTalkUser() {
          Logg.e("카카오톡 사용자가 아님");
        }

        override fun onSessionClosed(errorResult: ErrorResult?) {
          Logg.e("세션이 닫혀 있음: " + errorResult);
        }

        override fun onFailure(errorResult: ErrorResult?) {
          super.onFailure(errorResult)
          Logg.e("친구 조회 실패: " + errorResult);
        }

        override fun onSuccess(result: AppFriendsResponse?) {
          Logg.i("친구 조회 성공")

          for (friend in result!!.friends) {
            Logg.d(friend.toString())
            friendList.add(KakaoFriendInfo(friend))
            val uuid = friend.uuid // 메시지 전송 시 사용
          }

          SelectFriendRecyclerView.layoutManager = LinearLayoutManager(activity)
          SelectFriendRecyclerView.adapter = FriendListRecyclerViewAdapter(friendList, activity)
        }
      })
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.giftButton -> {
        val list = friendList.filter { it.selectFlag }
        list.forEach {
          //카톡을 보내요
          uuids.add(it.friendInfo!!.uuid)
          Logg.d(it.friendInfo!!.profileNickname)
        }
        sendKakaoMassage(uuids)
      }
    }
  }

  fun sendKakaoMassage(uuids: ArrayList<String>) {
    val textTemplate = TextTemplate.newBuilder(
      "안녕안녕",
      LinkObject.newBuilder().setAndroidExecutionParams("https://www.naver.com/").build()
    )
//      .addButton(ButtonObject("앱에서 보기",LinkObject.newBuilder().setWebUrl("https://www.naver.com/").setMobileWebUrl("https://www.naver.com/")))
    val link = LinkObject.newBuilder()
      .setWebUrl("https://www.naver.com/")
      .setMobileWebUrl("https://www.naver.com/")
      .build()
    val params: TemplateParams = TextTemplate.newBuilder("Text", link)
      .setButtonTitle("This is button")
      .build()
    KakaoTalkService.getInstance()
      .sendMessageToFriends(uuids, params, object : TalkResponseCallback<MessageSendResponse>() {
        override fun onNotKakaoTalkUser() {
          TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onSessionClosed(errorResult: ErrorResult?) {
          TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onFailure(errorResult: ErrorResult?) {
          super.onFailure(errorResult)
          Logg.i("친구에게 보내기 실패 $errorResult")
        }

        override fun onSuccess(result: MessageSendResponse?) {
          if (result!!.successfulReceiverUuids() != null) {
            Logg.i("친구에게 보내기 성공")
            Logg.d("전송에 성공한 대상: " + result.successfulReceiverUuids())
          }
        }
      })
  }
}
