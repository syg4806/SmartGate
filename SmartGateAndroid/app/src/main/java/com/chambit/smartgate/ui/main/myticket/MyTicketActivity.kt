package com.chambit.smartgate.ui.main.myticket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.MyTicketData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.network.GetTicketListener
import com.chambit.smartgate.ui.main.booking.BookingActivity
import com.chambit.smartgate.ui.main.booking.placelist.PlaceListActivity
import com.chambit.smartgate.ui.send.SendTicketActivity
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.MyProgressBar
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_my_ticket.*

class MyTicketActivity : AppCompatActivity() {
  val activity = this
  lateinit var bookingIntent: Intent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_my_ticket)
    bookingIntent = Intent(this, PlaceListActivity::class.java)

    val progressbar = MyProgressBar(activity)
    progressbar.show()

    val getTicketListener = object : GetTicketListener {
      override fun tickets(ticketDatas: ArrayList<TicketData>) {
      }

      override fun myTickets(
        myTicketDatas: ArrayList<MyTicketData>,
        ticketDatas: ArrayList<TicketData>
      ) {
        if (myTicketDatas.isEmpty()) {
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
            MyTicketRecyclerAdapter(myTicketDatas, activity)

        }
        progressbar.dismiss()
      }

      override fun getTicketReference(reference: DocumentReference) {
      }
    }
    FBTicketRepository().getMyTickets(getTicketListener)

  }
}
