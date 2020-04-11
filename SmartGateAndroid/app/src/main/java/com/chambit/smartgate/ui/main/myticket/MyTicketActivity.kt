package com.chambit.smartgate.ui.main.myticket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chambit.smartgate.R
import com.chambit.smartgate.network.FBTicket
import com.chambit.smartgate.ui.send.SendTicketActivity
import com.chambit.smartgate.util.Logg
import kotlinx.android.synthetic.main.activity_my_ticket.*

class MyTicketActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ticket)
        FBTicket().getMyTickets(this)

        myTicketEmptyTicketToSendTicket.setOnClickListener {
            val intent = Intent(baseContext, SendTicketActivity::class.java)
            startActivity(intent)
        }
    }
}
