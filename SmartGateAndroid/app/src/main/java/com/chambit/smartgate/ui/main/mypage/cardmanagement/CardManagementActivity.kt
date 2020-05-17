package com.chambit.smartgate.ui.main.mypage.cardmanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.CardData
import com.chambit.smartgate.ui.main.myticket.MyTicketRecyclerAdapter
import kotlinx.android.synthetic.main.activity_card_management.*
import kotlinx.android.synthetic.main.activity_used_ticket.*

class CardManagementActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_card_management)

    val cardDatas = arrayListOf<CardData>()

    //cardDatas.add(o)

   /* cardRecyclerView.layoutManager =
      LinearLayoutManager(baseContext, RecyclerView.HORIZONTAL, false)
    cardRecyclerView.adapter =
      MyTicketRecyclerAdapter(cardDatas)*/
  }
}
