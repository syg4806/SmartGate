package com.chambit.smartgate.ui.send

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chambit.smartgate.R
import com.chambit.smartgate.constant.Constants.PLACE_ID
import com.chambit.smartgate.constant.Constants.PLACE_NAME
import com.chambit.smartgate.dataClass.KakaoFriendInfo
import com.chambit.smartgate.dataClass.SendTicketData
import com.chambit.smartgate.network.BaseFB
import com.chambit.smartgate.network.FBPlaceRepository
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.ui.BaseActivity
import com.chambit.smartgate.ui.main.MainActivity
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.SharedPref
import com.kakao.friends.AppFriendContext
import com.kakao.friends.AppFriendOrder
import com.kakao.friends.response.AppFriendsResponse
import com.kakao.kakaotalk.callback.TalkResponseCallback
import com.kakao.kakaotalk.response.MessageSendResponse
import com.kakao.kakaotalk.v2.KakaoTalkService
import com.kakao.message.template.ButtonObject
import com.kakao.message.template.ContentObject
import com.kakao.message.template.FeedTemplate
import com.kakao.message.template.LinkObject
import com.kakao.network.ErrorResult
import kotlinx.android.synthetic.main.activity_send_ticket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SendTicketActivity : BaseActivity() {
  companion object {
    const val TICKET_LIST = "ticketList"
  }

  private val friendList = ArrayList<KakaoFriendInfo>()
  private var placeId: String? = null
  private var placeName: String? = null
  private lateinit var sendTicketList: Array<SendTicketData>
  private lateinit var sendTicketViewModel: SendTicketViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_send_ticket)
    sendTicketList = intent.getSerializableExtra(TICKET_LIST) as Array<SendTicketData>
    placeId = intent.getStringExtra(PLACE_ID)
    placeName = intent.getStringExtra(PLACE_NAME)
    sendTicketViewModel = ViewModelProvider(this).get(SendTicketViewModel::class.java)
    sendTicketViewModel.counter.postValue(sendTicketList.size)
    // TODO : 티켓 개수랑 사람 선택 버튼이랑 연결해서 숫자 바뀌게 하기
    sendTicketViewModel.counter.observe(this, Observer {
      selectNum.text = it?.toString()
      Logg.d(sendTicketList.size.toString())
      Logg.d(sendTicketList.get(0).toString())
    })
    getKakaoFriendList()
  }
  lateinit var selectedFriendList:List<KakaoFriendInfo>
  fun onClick(view: View) {
    when (view.id) {
      R.id.giftButton -> {
        selectedFriendList = friendList.filter { it.selectFlag }
        sendKakaoMassage(selectedFriendList.map { it.friendInfo!!.uuid })
      }
    }
  }

  private fun getKakaoFriendList() {
    // 컨텍스트 생성
//   - 닉네임, 처음(index 0)부터, 100명까지, 오름차순 예시
    val appFriendContext = AppFriendContext(AppFriendOrder.NICKNAME, 0, 100, "asc")

    // 조회 요청
    KakaoTalkService.getInstance()
      .requestAppFriends(appFriendContext, object : TalkResponseCallback<AppFriendsResponse>() {
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
            friendList.add(KakaoFriendInfo(friend, false))
          }

          SelectFriendRecyclerView.layoutManager = LinearLayoutManager(this@SendTicketActivity)
          SelectFriendRecyclerView.adapter =
            FriendListRecyclerViewAdapter(this@SendTicketActivity, 1, friendList)
        }
      })
  }

  private fun sendKakaoMassage(uuids: List<String>) {
    // 카톡 메시지 형식 설정
    launch {
      withContext(Dispatchers.IO) {
        FBPlaceRepository().getPlaceInfo(placeId!!).let {
          val imgUri = BaseFB().getImage(it.imagePath!!)
          Logg.d(imgUri.toString())
          // 카톡 메시지 형식 설정
          val feedTemplate = FeedTemplate
            .newBuilder(
              ContentObject.newBuilder(
                "선물 도착!!",
                imgUri.toString(),
                LinkObject.newBuilder().build()
              ).setDescrption("${SharedPref.userName}님이 ${placeName} 티켓을 선물했습니다.").build()
            ).addButton(
              ButtonObject("앱으로 이동", LinkObject.newBuilder().build())
            )
            .build()

          // 카톡 보내기
          KakaoTalkService.getInstance().sendMessageToFriends(uuids, feedTemplate,
            object : TalkResponseCallback<MessageSendResponse>() {

              override fun onNotKakaoTalkUser() {
                Logg.d("onNotKakaoTalkUser()")
              }

              override fun onSessionClosed(errorResult: ErrorResult?) {
                Logg.d("onSessionClosed()")
              }

              override fun onFailure(errorResult: ErrorResult?) {
                super.onFailure(errorResult)
                Logg.i("친구에게 보내기 실패 $errorResult")
              }

              override fun onSuccess(result: MessageSendResponse?) {

              }
            })
          launch {
            Logg.d("Start sending")
            Logg.d(selectedFriendList.joinToString { it.friendInfo!!.id.toString() })
            Logg.d(sendTicketList.joinToString { it.ticketId.toString() })
            selectedFriendList.forEachIndexed { index, kakaoFriendInfo ->
              FBTicketRepository().sendTicket(
                sendTicketList[index].certificateNo!!.toString(),
                kakaoFriendInfo.friendInfo?.id.toString()
              )
            }

          }

        }
      }
    }
  }

  override fun onBackPressed() {
    super.onBackPressed()
    startActivity(Intent(this, MainActivity::class.java).apply {
      this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    })
  }
}
