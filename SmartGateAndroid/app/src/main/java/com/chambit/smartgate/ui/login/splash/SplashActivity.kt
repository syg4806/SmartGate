package com.chambit.smartgate.ui.login.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.TicketGiftState
import com.chambit.smartgate.network.BaseFB
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.ui.login.LoginActivity
import com.chambit.smartgate.ui.main.MainActivity
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.SharedPref
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {
  var senderID: String? = null
  var senderCertificateNo: String? = null
  var sendTicketDateOfPurchase: String? = null
  var sendTicketExpirationDate: String? = null
  var ticketID: String? = null
  var placeID: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    if (intent.action == Intent.ACTION_VIEW) {
      val boardId = intent.data!!
      val key = boardId.toString().substring(boardId.toString().lastIndexOf("=") + 1)
      val keySplitArray: List<String> = key.split("/")
      Logg.d("받아 온 값? : ${boardId}") // 보낸 사람 Id/ 티켓 Id
      Logg.d("key? : ${key}")
      Logg.d("보낸 사람? : ${keySplitArray[0]}")
      Logg.d("보낸 사람 CertificateNo? : ${keySplitArray[1]}")
      Logg.d("구매일? : ${keySplitArray[2]}")
      Logg.d("만기일? : ${keySplitArray[3]}")
      Logg.d("티켓 id? : ${keySplitArray[4]}")
      Logg.d("장소 id? : ${keySplitArray[5]}")
      senderID = keySplitArray[0]
      senderCertificateNo = keySplitArray[1]
      sendTicketDateOfPurchase = keySplitArray[2]
      sendTicketExpirationDate = keySplitArray[3]
      ticketID = keySplitArray[4]
      placeID = keySplitArray[5]

      // 보낸 사람 티켓의 선물 상태를 "보냄"으로 변경
      FBTicketRepository().changeGiftState(senderID!!, senderCertificateNo!!, TicketGiftState.SENT)

      // 받는 사람은 부모 티켓 id로 티켓함에 티켓 생성
      /**
       *  ticketRef: DocumentReference,
      expirationDate: Long, // 만기일
      selectedDateFrom: Long, // 구매일
      ticketCount: Int
       */

      Logg.d(BaseFB().getTicketRef(placeID!!, ticketID!!).toString())

      FBTicketRepository().buyTicket(
        BaseFB().getTicketRef(placeID!!, ticketID!!),
        sendTicketExpirationDate!!.toLong(),
        sendTicketDateOfPurchase!!.toLong(),
        1
        ,
        TicketGiftState.RECEIVED
      )
      // TODO : 위의 값이 있으면 받아오기 성공이므로 (구매자쪽 티켓상태 : sended, 받는이 쪽 : 티켓상태 : Unused)으로 변경
    }
//    val gifImage =  GlideDrawableImageViewTarget(splashLogo);
    Glide.with(this).load(R.drawable.logo_gif).fitCenter().into(splashLogo)
    launchApp()
  }

  private fun launchApp() {
    /**
     *  Firestore 초기화
     */

//    Logg.i("프리퍼런스에 저장된 자동 로그인 유무 : z${UserInfo.autoLoginKey}b")
    Handler().postDelayed({
      // 앞의 과정이 약간의 시간이 필요하거나 한 경우 바로 어떤 명령을 실행하지 않고 잠시 딜레이를 갖고 실행
      /**
       *  // 로그인 고유 값이 있으면 --> 회원가입 진행 끝났다고 생각하고 일단ㄱㄱ -> 수정해야함
       */
      // 앱 설치시에는 isEmpty() 즉, 값이 없다.
      if (SharedPref.autoLoginKey.isEmpty()) { // 로그인 고유 값이 있으면 --> 회원가입 진행 끝났다고 생각하고 일단ㄱㄱ -> 수정해야함
        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        finish()
      } else {
        // shared에 로그인 ID 고유값이 없으면 초기 가입자 or (로그아웃 or 앱 삭제 후 재 로그인)
        // main으로

        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()

//          로그인 고유 값이 있는데 로그아웃으로 인한것이면 Db에서 개인 정보가 있는지 검사가 필요??
//         검사해서 데이터가 있으면...음....ㅠㅠ
      }
    }, 3000)
  }
}
