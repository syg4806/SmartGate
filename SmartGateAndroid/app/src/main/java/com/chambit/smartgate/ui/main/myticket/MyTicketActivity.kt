package com.chambit.smartgate.ui.main.myticket

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.R
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.ui.main.booking.placelist.PlaceListActivity
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.MyProgressBar
import kotlinx.android.synthetic.main.activity_my_ticket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyTicketActivity : AppCompatActivity() {
  val activity = this
  lateinit var bookingIntent: Intent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_my_ticket)
    bookingIntent = Intent(this, PlaceListActivity::class.java)

    val progressbar = MyProgressBar(activity)
    progressbar.show()
    MainScope().launch {
      val ownedTickets = withContext(Dispatchers.IO) {
        FBTicketRepository().listOwnedTickets()
      }
      Logg.d(ownedTickets.joinToString { it.certificateNo.toString() })
      if (ownedTickets.isEmpty()) {
        myTicketEmptyTicketView.visibility = View.VISIBLE
        myTicketEmptyTicketToSendTicket.setOnClickListener {
          startActivity(bookingIntent)
          finish()
        }
        progressbar.dismiss()
      } else {
        activity.myTicketEmptyTicketView.visibility = View.GONE
        //adpater 추가
        myTicketActivityRecyclerView.layoutManager =
          LinearLayoutManager(baseContext, RecyclerView.HORIZONTAL, false)
        myTicketActivityRecyclerView.adapter =
          MyTicketRecyclerAdapter(ownedTickets)
      }
      progressbar.dismiss()
    }
  }
}

