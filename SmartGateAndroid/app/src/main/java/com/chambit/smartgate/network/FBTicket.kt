package com.chambit.smartgate.network

import android.app.Activity
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.ui.main.myticket.MyTicketRecyclerAdapter
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.SharedPref
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_my_ticket.*

class FBTicket {
    val db = FirebaseFirestore.getInstance()


    fun getMyTickets(activity: Activity) {
        db.collection("userinfo").document(SharedPref.autoLoginKey).collection("tickets")
            .get()
            .addOnSuccessListener { result ->
                val ticketDatas = arrayListOf<TicketData>()

                for (document in result) {
                    val ticketData = document.toObject(TicketData::class.java)
                    ticketDatas.add(ticketData)
                }
                Logg.d("ssmm11 activity = $activity")

                activity.myTicketActivityRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                activity.myTicketActivityRecyclerView.adapter = MyTicketRecyclerAdapter(ticketDatas)
            }
    }
}