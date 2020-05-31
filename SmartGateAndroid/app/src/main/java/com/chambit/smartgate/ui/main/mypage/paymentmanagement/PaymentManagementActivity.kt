package com.chambit.smartgate.ui.main.mypage.paymentmanagement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.util.Logg
import kotlinx.android.synthetic.main.activity_payment_management.*

class PaymentManagementActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_payment_management)

    toggleButton.setOnCheckedChangeListener { p0, isChecked ->
      if(isChecked){
        Logg.d("체크 됨")
      }else{
        Logg.d("해제 됨.")
      }
    }
  }
}
