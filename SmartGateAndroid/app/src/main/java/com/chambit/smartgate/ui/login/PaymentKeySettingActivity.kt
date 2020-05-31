package com.chambit.smartgate.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.chambit.smartgate.R
import com.chambit.smartgate.network.FBUsersRepository
import com.chambit.smartgate.ui.main.MainActivity
import com.chambit.smartgate.util.SharedPref
import kotlinx.android.synthetic.main.activity_booking.*
import kotlinx.android.synthetic.main.activity_booking.paymentButton
import kotlinx.android.synthetic.main.activity_payment_key_setting.*

class PaymentKeySettingActivity : AppCompatActivity(), View.OnClickListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_payment_key_setting)


    paymentButton.setOnClickListener(this)
  }
  override fun onClick(v: View?) {
    when (v!!.id) {
      R.id.paymentButton -> {
        val key = paymentKeyEditText.text.toString()
        val email = intent.getStringExtra("email")
        if (key.length < 8) {
          Toast.makeText(this, "비밀 번호는 8자리 이상이어야 합니다.", Toast.LENGTH_LONG).show()
        }
        else {
          //TODO: 메인 액티비티로 넘어가서 할 일
          SharedPref.paymentKey = key
          FBUsersRepository().userSignUp(email!!)
          val intent = Intent(baseContext, MainActivity::class.java)
          startActivity(intent)
          finish()
        }
      }
    }
  }
}
