package com.chambit.smartgate.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.chambit.smartgate.R
import com.chambit.smartgate.ui.main.booking.BookingActivity
import com.chambit.smartgate.ui.main.mypage.MyPageActivity
import com.chambit.smartgate.ui.main.myticket.MyTicketActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        when(view.id){
            R.id.toBooking->{
                val intent= Intent(this,BookingActivity::class.java)
                startActivity(intent)
            }
            R.id.toMyTicket->{
                val intent= Intent(this, MyTicketActivity::class.java)
                startActivity(intent)
            }
            R.id.toMyPage->{
                val intent= Intent(this,MyPageActivity::class.java)
                startActivity(intent)
            }
        }
    }
}