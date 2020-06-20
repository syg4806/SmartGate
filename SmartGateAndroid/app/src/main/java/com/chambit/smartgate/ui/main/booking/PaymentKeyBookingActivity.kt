package com.chambit.smartgate.ui.main.booking

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.chambit.smartgate.ui.login.PaymentKeySettingActivity
import com.chambit.smartgate.util.SharedPref
import kotlinx.android.synthetic.main.activity_payment_key_setting.*

class PaymentKeyBookingActivity : View.OnClickListener, PaymentKeySettingActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    paymentKeyTextView.text = "결제 비밀 번호 입력"
  }

  override fun check(mode: Boolean) {
    clickCheck(password.length, mode)

    if (password.length == 6) {
      if (SharedPref.paymentKey == password) {
        setResult(100)
        finish()
      } else {
        Toast.makeText(this, "비밀 번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
        password = ""
        imageInit()
        numberList.shuffle()
        setNumber(numberList)
      }
    }
  }
}
