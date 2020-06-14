package com.chambit.smartgate.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.ui.main.booking.placelist.PlaceListActivity
import com.chambit.smartgate.ui.main.mypage.MyPageActivity
import com.chambit.smartgate.ui.main.myticket.MyTicketActivity
import com.chambit.smartgate.ui.beacon.TicketUsingActivity
import com.chambit.smartgate.util.SetDataActivity

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.logo -> {
        val intent = Intent(this, SetDataActivity::class.java)
        startActivity(intent)
      }
      R.id.toBooking -> {
        val intent = Intent(
          this,
          PlaceListActivity::class.java
        )
        startActivity(intent)
      }
      R.id.toMyTicket -> {
        val intent = Intent(this, MyTicketActivity::class.java)
        startActivity(intent)
      }
      R.id.toMyPage -> {
        val intent = Intent(this, MyPageActivity::class.java)
        startActivity(intent)
      }
    }
  }
}
