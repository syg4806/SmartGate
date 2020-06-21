package com.chambit.smartgate.ui.main.mypage.paymentmanagement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.util.SharedPref
import kotlinx.android.synthetic.main.activity_payment_management.*

class PaymentManagementActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_payment_management)

    toggleButton.isChecked = SharedPref.useFingerPrint

    toggleButton.setOnCheckedChangeListener { _, isChecked ->
      SharedPref.useFingerPrint = isChecked
    }

    confirm_button.setOnClickListener {
      finish()
    }
  }
}
