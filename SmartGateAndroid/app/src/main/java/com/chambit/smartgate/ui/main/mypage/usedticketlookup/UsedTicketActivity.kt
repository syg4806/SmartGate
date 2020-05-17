package com.chambit.smartgate.ui.main.mypage.usedticketlookup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.R
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.ui.main.myticket.MyTicketRecyclerAdapter
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.MyProgressBar
import kotlinx.android.synthetic.main.activity_my_ticket.*
import kotlinx.android.synthetic.main.activity_my_ticket.myTicketEmptyTicketToSendTicket
import kotlinx.android.synthetic.main.activity_used_ticket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsedTicketActivity : AppCompatActivity() {
  val activity = this
  lateinit var bookingIntent: Intent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_used_ticket)

    val progressbar = MyProgressBar(activity)
    progressbar.show()
    MainScope().launch {
      val ownedTickets = withContext(Dispatchers.IO) {
        FBTicketRepository().listOwnedTickets(true)
      }
      Logg.d(ownedTickets.joinToString { it.certificateNo.toString() })
      if (ownedTickets.isEmpty()) {
        usedTicketEmptyTicketView.visibility = View.VISIBLE
        progressbar.dismiss()
      } else {
        activity.usedTicketEmptyTicketView.visibility = View.GONE
        //adpater 추가
        usedTicketActivityRecyclerView.layoutManager =
          LinearLayoutManager(baseContext, RecyclerView.HORIZONTAL, false)
        usedTicketActivityRecyclerView.adapter =
          MyTicketRecyclerAdapter(ownedTickets)
      }
      progressbar.dismiss()
    }
  }
}
