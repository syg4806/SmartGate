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
import kotlinx.coroutines.*

class MyTicketActivity : AppCompatActivity(), CoroutineScope by MainScope() {
  val activity = this
  lateinit var bookingIntent: Intent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_my_ticket)
    bookingIntent = Intent(this, PlaceListActivity::class.java)

    val progressbar = MyProgressBar(activity)
    progressbar.show()
    launch {
      val ownedTickets = withContext(Dispatchers.IO) {
        FBTicketRepository().listOwnedTickets(false)
      }
      Logg.d(ownedTickets.joinToString { it.certificateNo.toString() })
      if (ownedTickets.isEmpty()) {  // 구매한 티켓이 없을 때
        myTicketEmptyTicketView.visibility = View.VISIBLE
        myTicketEmptyTicketToSendTicket.setOnClickListener {
          startActivity(bookingIntent)
          finish()
        }
        progressbar.dismiss()
      } else { // 구매한 티켓이 존재할 때
        activity.myTicketEmptyTicketView.visibility = View.GONE
        //adpater 추가
        myTicketActivityRecyclerView.layoutManager =
          // reverseLayout: 아이템이 보이는 방향. true 지정시 아래에서 위로 올라감
          LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        myTicketActivityRecyclerView.adapter =
          MyTicketRecyclerAdapter(ownedTickets, activity)
      }
      progressbar.dismiss()
    }
  }
}

