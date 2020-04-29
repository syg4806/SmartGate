package com.chambit.smartgate.util

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.PlaceInfoData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.network.FBPlaceRepository
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.network.SetDataListener
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_set_data.*
import java.util.*

class SetDataActivity : AppCompatActivity() {


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_set_data)
    val progressbar = MyProgressBar(this)

    /**
     * 셋팅이 완료가 되면 리스너가 실행되고
     * 돌고있던 프로그래스바가 사라지게 된다.
     */
    val setDataListener = object : SetDataListener {
      override fun setPlaceData() {
        progressbar.dismiss()
      }

      override fun setTicketData() {
        progressbar.dismiss()
      }
    }
    /**
     * 티켓 셋팅 버튼 클릭시
     */
    ticketDataSettingButton.setOnClickListener {
      progressbar.show()
      val dt = Date()



      val ticketData = TicketData(
        dt.time.toString(),
        null,
        setTicketKind.text.toString(),
        "ticketImage/" + dt.time.toString()
      )

      // 셋 티켓 함수 실행
      FBTicketRepository().setTicket(ticketData, setDataListener)
    }


    /**
     * 장소 셋팅 버튼 클릭 시
     */


    placeDataSettingButton.setOnClickListener {
      val date = Date()
      progressbar.show()
      val placeInfoData = PlaceInfoData(
        date.time.toString(),
        setname.text.toString(),
        setPlaceDisciption.text.toString(),
        "placeLogo/" + setname.text.toString() + date.time + ".jpg",
        "placeImage/" + setname.text.toString() + date.time + ".jpg"
      )

      FBPlaceRepository().setPlace(placeInfoData, setDataListener)
    }
  }
}
