package com.chambit.smartgate.ui.main.mypage.usedticketlookup

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.BaseActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.ui.main.myticket.MyTicketRecyclerAdapter
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.MyProgressBar
import kotlinx.android.synthetic.main.activity_used_ticket.*
import kotlinx.coroutines.launch

class UsedTicketActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_used_ticket)

    val progressbar = MyProgressBar(this)
    progressbar.show()
    launch {
      val ownedTickets =
        FBTicketRepository().listOwnedTickets(true)

      Logg.d(ownedTickets.joinToString { it.certificateNo.toString() })
      if (ownedTickets.isEmpty()) {
        usedTicketEmptyTicketView.visibility = View.VISIBLE
        progressbar.dismiss()
      } else {
        this@UsedTicketActivity.usedTicketEmptyTicketView.visibility = View.GONE
        //adpater 추가
        usedTicketActivityRecyclerView.layoutManager =
          LinearLayoutManager(baseContext, RecyclerView.HORIZONTAL, false)
        usedTicketActivityRecyclerView.adapter =
          MyTicketRecyclerAdapter(this@UsedTicketActivity, ownedTickets)
      }
      progressbar.dismiss()
    }
  }
}
