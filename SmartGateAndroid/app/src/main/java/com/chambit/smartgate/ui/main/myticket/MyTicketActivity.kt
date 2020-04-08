package com.chambit.smartgate.ui.main.myticket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chambit.smartgate.R
import com.chambit.smartgate.network.FBTicket
import com.chambit.smartgate.util.Logg

class MyTicketActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ticket)
        Logg.d("ssmm11 myticketactivity =")
        FBTicket().getMyTickets(this)
    }
}
