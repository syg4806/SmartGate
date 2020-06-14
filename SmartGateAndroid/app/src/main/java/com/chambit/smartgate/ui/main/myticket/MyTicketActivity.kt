package com.chambit.smartgate.ui.main.myticket

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.BaseActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.extensions.gone
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.ui.main.booking.placelist.PlaceListActivity
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.MyProgressBar
import kotlinx.android.synthetic.main.activity_my_ticket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyTicketActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_my_ticket)
    myTicketActivityRecyclerView.layoutManager =
      LinearLayoutManager(baseContext, RecyclerView.HORIZONTAL, false)
  }

  override fun onResume() {
    super.onResume()
    val progressbar = MyProgressBar(this)
    progressbar.show()
    launch {
      val ownedTickets = withContext(Dispatchers.IO) {
        FBTicketRepository().listOwnedTickets(false)
      }
      Logg.d(ownedTickets.joinToString { it.certificateNo.toString() })
      if (ownedTickets.isEmpty()) {
        myTicketEmptyTicketView.visibility = View.VISIBLE
        myTicketEmptyTicketToSendTicket.setOnClickListener {
          startActivity(Intent(this@MyTicketActivity, PlaceListActivity::class.java))
          finish()
        }
        myTicketActivityRecyclerView.gone()
        progressbar.dismiss()
      } else {
        (this@MyTicketActivity).myTicketEmptyTicketView.visibility = View.GONE
        //adpater 추가

        myTicketActivityRecyclerView.adapter =
          MyTicketRecyclerAdapter(this@MyTicketActivity, ownedTickets)
      }
      progressbar.dismiss()
    }
  }
}

