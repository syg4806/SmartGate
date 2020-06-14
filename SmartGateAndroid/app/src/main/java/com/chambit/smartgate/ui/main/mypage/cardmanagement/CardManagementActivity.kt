package com.chambit.smartgate.ui.main.mypage.cardmanagement

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import kotlinx.android.synthetic.main.activity_card_management.*

class CardManagementActivity : AppCompatActivity(), View.OnClickListener {


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_card_management)

    cardImageView.setOnClickListener(this)
  }

  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.cardImageView -> {
        startActivity(Intent(this@CardManagementActivity, CardAddActivity::class.java))
      }
    }
  }
}
