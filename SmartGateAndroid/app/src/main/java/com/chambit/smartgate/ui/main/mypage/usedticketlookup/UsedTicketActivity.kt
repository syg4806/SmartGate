package com.chambit.smartgate.ui.main.mypage.usedticketlookup

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.TicketState
import com.chambit.smartgate.extensions.gone
import com.chambit.smartgate.extensions.longToast
import com.chambit.smartgate.extensions.visible
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.ui.BaseActivity
import com.chambit.smartgate.ui.main.myticket.MyTicketRecyclerAdapter
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.MyProgressBar
import kotlinx.android.synthetic.main.activity_used_ticket.*
import kotlinx.coroutines.launch

class UsedTicketActivity : BaseActivity() {
  companion object {
    const val USETICKET = "useTicket"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_used_ticket)
    if (intent.hasExtra(USETICKET)) {
      this.longToast("게이트의 불을 확인하고, 통과해주세요.")
    }
    val progressbar = MyProgressBar(this)
    progressbar.show()
    launch {
      val ownedTickets =
        FBTicketRepository().listOwnedTickets(TicketState.USED)

      Logg.d(ownedTickets.joinToString { it.certificateNo.toString() })
      if (ownedTickets.isEmpty()) {
        usedTicketEmptyTicketView.visible()
        progressbar.dismiss()
      } else {
        this@UsedTicketActivity.usedTicketEmptyTicketView.gone()
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
