package com.chambit.smartgate.ui.main.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.ui.main.mypage.cardmanagement.CardManagementActivity
import com.chambit.smartgate.ui.main.mypage.paymentmanagement.PaymentManagementActivity
import com.chambit.smartgate.ui.main.mypage.usedticketlookup.UsedTicketActivity
import com.chambit.smartgate.ui.main.myticket.MyTicketActivity

class MyPageActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_my_page)
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.preUseTicketButton -> { // 사용 전 티켓
        startActivity(Intent(this, MyTicketActivity::class.java))
      }
      R.id.usedTicketLookupButton -> {  // 사용한 티켓 조회
        startActivity(Intent(this, UsedTicketActivity::class.java))
      }
      R.id.cardRegistrationAndInquiryButton -> { // 카드등록 및 조회
        startActivity(Intent(this, CardManagementActivity::class.java))
      }
      R.id.simplePaymentMethodManagementButton -> {  // 간편결제방식 관리
        startActivity(Intent(this, PaymentManagementActivity::class.java))
      }
    }
  }
}
