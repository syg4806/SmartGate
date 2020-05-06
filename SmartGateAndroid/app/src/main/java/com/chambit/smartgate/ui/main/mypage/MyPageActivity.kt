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
        val nextIntent = Intent(this, MyTicketActivity::class.java)
        startActivity(nextIntent)
      }
      R.id.usedTicketLookupButton -> {  // 사용한 티켓 조회
        val nextIntent = Intent(this, UsedTicketActivity::class.java)
        startActivity(nextIntent)
      }
      R.id.cardRegistrationAndInquiryButton -> { // 카드등록 및 조회
        val nextIntent = Intent(this, CardManagementActivity::class.java)
        startActivity(nextIntent)
      }
      R.id.simplePaymentMethodManagementButton -> {  // 간편결제방식 관리
        val nextIntent = Intent(this, PaymentManagementActivity::class.java)
        startActivity(nextIntent)
      }
    }
  }
}
