package com.chambit.smartgate.ui.main.mypage.cardmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.CardData
import com.chambit.smartgate.ui.main.myticket.MyTicketActivity
import com.chambit.smartgate.ui.main.myticket.MyTicketRecyclerAdapter
import kotlinx.android.synthetic.main.activity_card_management.*
import kotlinx.android.synthetic.main.activity_used_ticket.*

class CardManagementActivity : AppCompatActivity(), View.OnClickListener {
  lateinit var nextIntent: Intent


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_card_management)

    nextIntent = Intent(this, CardAddActivity::class.java)


    cardImageView.setOnClickListener(this)
  }

  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.cardImageView -> {
        startActivity(nextIntent)
      }
    }
  }
}
