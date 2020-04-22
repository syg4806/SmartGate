package com.chambit.smartgate.ui.main.booking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.chambit.smartgate.R

class PlaceInformationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_information)
    }

    fun onClick(view: View){
        when(view.id){
            R.id.toReserveButton->{
                val nextIntent = Intent(this, BookingActivity::class.java)
                startActivity(nextIntent)
            }
        }
    }
}
