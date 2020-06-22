package com.chambit.smartgate.ui.send

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.KakaoFriendInfo
import com.chambit.smartgate.dataClass.TicketGiftState
import com.chambit.smartgate.network.BaseFB
import com.chambit.smartgate.network.FBPlaceRepository
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.ui.BaseActivity
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SendTicketActivity : BaseActivity() {
  private val activity = this
  private val friendList = ArrayList<KakaoFriendInfo>()
  private val uuids = ArrayList<String>()
  private var friendName: String? = null
  private var placeId: String? = null
  private var ticketId: String? = null
  private var ticketKinds: String? = null
  private var placeName: String? = null
  private var certificateNo: Long? = null
  private var expirationDate: Long? = null // 만기일
  private var dateOfPurchase: Long? = null // 구매일
  private var fromBookingFlag: Boolean? = null // 예약하기 페이지에서 왔는지 판별하는 flag
  private var sendTicketList: Array<Parcelable>? = null
  // 팝업 띄우는 함수
  private lateinit var noticePopup: ChoicePopUp // 전역으로 선언하지 않으면 리스너에서 dismiss 사용 불가.

  private var sendTicketViewModel: SendTicketViewModel? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_send_ticket)

    // 예약하기에서 왔을 때 뒤로가기하면 실행되도록 하기위한 flag
    fromBookingFlag = intent.getBooleanExtra("goToSendActivity", false) // 기본 값은 false

    Logg.d(fromBookingFlag.toString())

    // 예약하기에서 왔으면 실행
    if (fromBookingFlag!!) {

      // 예약하기에서 받은 리스트 (ticketRef, certificateNo, expirationDate)을 받음
      //TODO : 생성된 Ticket고유 값을 받는게 아니라서 수정해야 할듯
      sendTicketList = intent.getParcelableArrayExtra("sendTicketListFromBooking")

      // 예약하기에서 넘어온 리스트 갯수만 큼 구매 가능한 티켓 수를 표시해주기 위한 viewModel
      sendTicketViewModel = ViewModelProvider(this).get(SendTicketViewModel::class.java)

      // 예약하기에서 안왔을 때는 이건 실행 안되게 해야하므로 null 인정
      sendTicketViewModel!!.CounterViewModel(sendTicketList?.size)

      sendTicketViewModel!!.counter.observe(this, Observer {
        selectNum.text = it?.toString()
      })
    }
    Logg.d(sendTicketList?.size.toString())
    Logg.d(sendTicketList?.get(0).toString())

    // MyTicketRecyclerAdapter에서 받은 값들
    ticketId = intent.getStringExtra("ticketId")
    ticketKinds = intent.getStringExtra("ticketKinds")
    placeName = intent.getStringExtra("placeName")
    placeId = intent.getStringExtra("placeId")
    dateOfPurchase = intent.getLongExtra("dateOfPurchase", 0L)
    expirationDate = intent.getLongExtra("expirationDate", 0L)
    certificateNo = intent.getLongExtra("certificateNo", 0L)
    Logg.d("${ticketId}")
    Logg.d("${ticketKinds}")
    Logg.d("${placeName}")
    Logg.d("${placeId}")
    Logg.d("${dateOfPurchase}")
    Logg.d("${expirationDate}")
    Logg.d("${certificateNo}")
    Logg.d("ticketId ? $ticketId , placeId ? ${placeId}")

    //TODO : 예약하기에서는 (ticketRef, certificateNo, expirationDate)을 리스트로 받아서 갯수로 나타냄. MyTicketRecyclerAdapter에서는 위의 7가지 값을 가져오므로 이걸로 갯수 나타내도 될듯
//    sendTicketViewModel!!.CounterViewModel()


    getKakaoFriendList()
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.giftButton -> {
        val list = friendList.filter { it.selectFlag }
        var friendName: String? = null
        list.forEach {
          //카톡을 보내요
          Logg.d(it.friendInfo!!.uuid)
          uuids.add(it.friendInfo!!.uuid)
          Logg.d(it.friendInfo!!.profileNickname)
          friendName = it.friendInfo!!.profileNickname
        }
        //TODO : 팝업 띄우고 확인받고 보내기
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
        sendKakaoMassage(uuids, friendName!!)
      }
      /**giftButton*/
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
            friendName = friend.profileNickname
            friendList.add(KakaoFriendInfo(friend))
          }

          SelectFriendRecyclerView.layoutManager = LinearLayoutManager(activity)
          SelectFriendRecyclerView.adapter = FriendListRecyclerViewAdapter(activity, 1, friendList)
//          SelectFriendRecyclerView.adapter.noti
        }
      })
  }

  var imgUri: Uri? = null
  private fun sendKakaoMassage(uuids: ArrayList<String>, friendName: String) {
    // 카톡 메시지 형식 설정
    launch {
      withContext(Dispatchers.IO) {
        FBPlaceRepository().getPlaceInfo(placeId!!).let {
          imgUri = BaseFB().getImage(it.imagePath!!)
          Logg.d(imgUri.toString())


          // 카톡 메시지 형식 설정
          val feedTemplate = FeedTemplate
            .newBuilder(
              ContentObject.newBuilder(
                "선물 도착!!",
                imgUri.toString(),
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
                  .setAndroidExecutionParams("key1=${SharedPref.autoLoginKey}/${certificateNo}/${dateOfPurchase}/${expirationDate}/${ticketId}/${placeId}") // 메시지로 전달되는 값. Splash에서 받음
                  .setIosExecutionParams("key2=value2")
                  .build()
              )
            )
            .build()


          // 카톡 보내기
          KakaoTalkService.getInstance().sendMessageToFriends(uuids,feedTemplate,
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
                  if (result!!.successfulReceiverUuids() != null) {
                    //TODO : 카톡은 보내지는데 이쪽 성공하기가 안뜸;;
                    Logg.i("친구에게 보내기 성공")
                    Logg.d("전송에 성공한 대상: " + result!!.successfulReceiverUuids())
//                    launch {
//                      if (FBTicketRepository().useTicket(certificateNo!!)) {
                        Logg.d("fdakjlj;faf")
                        FBTicketRepository().changeGiftState(
                          SharedPref.autoLoginKey,
                          certificateNo.toString(),
                          TicketGiftState.PRESENTING
                        )
//                      }
//                    }
                  }
                }
              })
        }
      }
    }
  }

  override fun onBackPressed() {
    super.onBackPressed()
    if (fromBookingFlag!!) { // true이면
      startActivity(Intent(this, MainActivity::class.java).apply {
        this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      })
    }
  }
}
