package com.chambit.smartgate.ui.send

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.KakaoFriendInfo
import com.chambit.smartgate.ui.main.MainActivity
import com.chambit.smartgate.util.ChoicePopUp
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


class SendTicketActivity : AppCompatActivity() {
  private val activity = this
  private val friendList = ArrayList<KakaoFriendInfo>()
  private val uuids = ArrayList<String>()
  private var friendName: String? = null
  private var ticketId: String? = null
  private var ticketKinds: String? = null
  private var placeName : String? = null
  private var fromBookingFlag : Boolean? = null // 예약하기 페이지에서 왔는지 판별하는 flag
  private var sendTicketList : Array<Parcelable>? = null
  // 팝업 띄우는 함수
  private lateinit var noticePopup: ChoicePopUp // 전역으로 선언하지 않으면 리스너에서 dismiss 사용 불가.

  private var sendTicketViewModel : SendTicketViewModel? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_send_ticket)

    fromBookingFlag = intent.getBooleanExtra("goToSendActivity", false) // 기본 값은 false
    sendTicketList = intent.getParcelableArrayExtra("sendTicketList")

    sendTicketViewModel = ViewModelProvider(this).get(SendTicketViewModel::class.java)
    sendTicketViewModel!!.CounterViewModel(sendTicketList?.size)

    sendTicketViewModel!!.counter.observe(this, Observer {
      selectNum.text = it.toString()
    })
    Logg.d(sendTicketList?.size.toString())
    Logg.d(sendTicketList?.get(0).toString())

    ticketId = intent.getStringExtra("ticketId")
    ticketKinds = intent.getStringExtra("ticketKinds")
    placeName = intent.getStringExtra("placeName")
    Logg.d("ticketId ? $ticketId")
    getKakaoFriendList()
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
//        noticePopup = ChoicePopUp(this,
//          "선물 보내기. \n\n[${placeInfoData.name},${ticketKindSpinner.selectedItem}, ${ticketCountSpinner.selectedItem} 개]",
//          View.OnClickListener {
//            FBTicketRepository().buyTicket(
//              tickets[ticketNo].placeRef!!.collection(
//                "tickets"
//              ).document(tickets[ticketNo].id!!), 0L, setMyTicketCount
//            )
//            finish()
//          },
//          View.OnClickListener {
//            noticePopup.dismiss()
//          })
//        noticePopup.show()
        sendKakaoMassage(uuids)
      }
    }
  }

  private fun getKakaoFriendList(){
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
            friendName = friend.profileNickname
            friendList.add(KakaoFriendInfo(friend))
          }

          SelectFriendRecyclerView.layoutManager = LinearLayoutManager(activity)
          SelectFriendRecyclerView.adapter = FriendListRecyclerViewAdapter(activity,1,friendList)
//          SelectFriendRecyclerView.adapter.noti
        }
      })
  }
  private fun sendKakaoMassage(uuids: ArrayList<String>) {
    // 카톡 메시지 형식 설정
    val feedTemplate = FeedTemplate
      .newBuilder(
        ContentObject.newBuilder(
          "선물 도착!!",
          "https://firebasestorage.googleapis.com/v0/b/smartgate-60162.appspot.com/o/placeImage%2F%EB%A1%AF%EB%8D%B0%EC%9B%94%EB%93%9C1588158443705.jpg?alt=media&token=c74eaca2-bdd1-4040-a634-507589f34c53",
          LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
            .setMobileWebUrl("https://developers.kakao.com").build()
        )
          .setDescrption("${SharedPref.userName}님이 ${friendName}님에게 ${placeName} 티켓을 선물했습니다.")
          .build()
      )
      .addButton(
        ButtonObject(
          "앱으로 이동", LinkObject.newBuilder()
            .setWebUrl("'https://developers.kakao.com")
            .setMobileWebUrl("https://developers.kakao.com")
            //${SharedPref.autoLoginKey}/${ticketId}
            .setAndroidExecutionParams("key1=${SharedPref.autoLoginKey}/${ticketId}") // 메시지로 전달되는 값. Splash에서 받음
            .setIosExecutionParams("key2=value2")
            .build()
        )
      )
      .build()

    // 카톡 보내기
    KakaoTalkService.getInstance()
      .sendMessageToFriends(uuids, feedTemplate, object : TalkResponseCallback<MessageSendResponse>() {
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
            //TODO : 선물 보내기 성공이므로 서버에 구매자 쪽 상태 : sending으로 변경하기
            Logg.i("친구에게 보내기 성공")
            Logg.d("전송에 성공한 대상: " + result.successfulReceiverUuids())

          }
        }
      })
  }

  override fun onBackPressed() {
    super.onBackPressed()

    if(fromBookingFlag!!){ // true이면
      startActivity(Intent(this, MainActivity::class.java).apply {
        this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      })
    }
  }
}
