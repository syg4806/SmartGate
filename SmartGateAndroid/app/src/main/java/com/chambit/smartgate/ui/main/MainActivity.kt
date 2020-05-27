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

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == 100) {
      startActivity(Intent(this, MyTicketActivity::class.java))
    }
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.logo -> {
        startActivity(Intent(this, SetDataActivity::class.java))
      }
      R.id.toBooking -> {
        startActivityForResult(Intent(this, PlaceListActivity::class.java), 100)
      }
      R.id.toMyTicket -> {
        startActivity(Intent(this, MyTicketActivity::class.java))
      }
      R.id.toMyPage -> {
        startActivity(Intent(this, MyPageActivity::class.java))
      }
      R.id.toBLE -> {
        val intent = Intent(this, TicketUsingActivity::class.java)
        startActivity(intent)
      }
    }
  }
}
