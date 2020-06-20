package com.chambit.smartgate.util

import android.os.Bundle
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.PlaceData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.network.FBPlaceRepository
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_set_data.*
import kotlinx.coroutines.launch
import java.util.*

class SetDataActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_set_data)
    val progressbar = MyProgressBar(this)

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

      launch {
        /**
         * 셋팅이 완료가 되면 돌고있던 프로그래스바가 사라지게 된다.
         */
        // 셋 티켓 함수 실행
        FBTicketRepository().setTicket(ticketData).let {
          progressbar.dismiss()
        }
      }
    }

    /**
     * 장소 셋팅 버튼 클릭 시
     */

    placeDataSettingButton.setOnClickListener {
      val date = Date()
      progressbar.show()
      val placeInfoData = PlaceData(
        date.time.toString(),
        setname.text.toString(),
        setPlaceDisciption.text.toString(),
        "placeLogo/" + setname.text.toString() + date.time + ".jpg",
        "placeImage/" + setname.text.toString() + date.time + ".jpg"
      )

      FBPlaceRepository().setPlace(placeInfoData).let { progressbar.dismiss() }
    }
  }
}
