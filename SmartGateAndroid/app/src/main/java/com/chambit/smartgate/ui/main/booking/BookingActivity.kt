package com.chambit.smartgate.ui.main.booking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.MyTicketData
import com.chambit.smartgate.dataClass.PlaceInfoData
import com.chambit.smartgate.dataClass.PlaceListData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.network.FBPlaceImageRepository
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.network.GetPlaceListener
import com.chambit.smartgate.network.GetTicketListener
import com.chambit.smartgate.ui.main.booking.placelist.PlaceListRecyclerViewAdapter
import com.chambit.smartgate.ui.main.myticket.MyTicketActivity
import com.chambit.smartgate.util.ChoicePopUp
import kotlinx.android.synthetic.main.activity_booking.*
import kotlinx.android.synthetic.main.activity_booking.view.*
import kotlinx.android.synthetic.main.activity_place_list.*

class BookingActivity : AppCompatActivity(), View.OnClickListener {
  var placeInfoData = PlaceInfoData()
  val activity = this

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_booking)

    placeInfoData = intent.getParcelableExtra("PlaceInfoData")!!
    FBPlaceImageRepository().getPlaceImage(bookingPlaceLogo, placeInfoData.placeImagePath!!, this)
    bookingPlaceName.text = placeInfoData.placeName

    FBTicketRepository().getTickets(placeInfoData.placeName!!, getTicketListener)

    paymentButton.setOnClickListener(this)
  }

  // 팝업 띄우는 함수
  lateinit var noticePopup: ChoicePopUp // 전역으로 선언하지 않으면 리스너에서 dismiss 사용 불가.

  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.paymentButton -> {
        noticePopup = ChoicePopUp(this, "티켓구매",
          "티켓을 구매했습니다. \n\n[장소,종류,날짜,장 수]",
          "확인", "선물하기",
          View.OnClickListener {
            val nextIntent = Intent(this, MyTicketActivity::class.java)
            startActivity(nextIntent)
            finish()
          },
          View.OnClickListener {
            noticePopup.dismiss()
          })
        noticePopup.show()
      }
    }
  }

  val getTicketListener = object : GetTicketListener {
    override fun tickets(ticketDatas: ArrayList<TicketData>) {
      val ticketKinds = arrayListOf<String>()
      val ticketDates = arrayListOf<String>()
      val ticketCounts = arrayListOf<String>()
      ticketDatas.forEach {
        ticketKinds.add(it.ticketKinds!!)
        ticketDates.add(it.ticketDate!!)
      }
      for (i in 1..5) {
        ticketCounts.add(i.toString())
      }
      var arrayAdapter =
        ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, ticketKinds)

      ticketKindSpinner.adapter = arrayAdapter

      arrayAdapter =
        ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, ticketDates)
      ticketDateSpinner.adapter = arrayAdapter

      arrayAdapter =
        ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, ticketCounts)
      ticketCountSpinner.adapter = arrayAdapter

    }

    override fun myTickets(myTicketData: ArrayList<MyTicketData>) {

    }
  }

}
