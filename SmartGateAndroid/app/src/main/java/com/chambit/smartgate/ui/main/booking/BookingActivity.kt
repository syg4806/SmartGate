package com.chambit.smartgate.ui.main.booking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.constant.Constants.PLACE_ID
import com.chambit.smartgate.dataClass.MyTicketData
import com.chambit.smartgate.dataClass.PlaceInfoData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.dataClass.TicketState
import com.chambit.smartgate.network.*
import com.chambit.smartgate.ui.main.myticket.MyTicketActivity
import com.chambit.smartgate.util.ChoicePopUp
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_booking.*
import kotlinx.android.synthetic.main.activity_place_information.*
import java.util.*
import kotlin.collections.ArrayList

class BookingActivity : AppCompatActivity(), View.OnClickListener {
  var placeInfoData = PlaceInfoData()
  lateinit var id:String
  lateinit var tickets: ArrayList<TicketData>
  val activity = this
  var setMyTicketCount = 0
  lateinit var nextIntent: Intent


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_booking)

    nextIntent = Intent(this, MyTicketActivity::class.java)
    id = intent.getStringExtra(PLACE_ID)!!
    FBPlaceRepository().getPlaceInfo(id){
      placeInfoData=it
      FBPlaceImageRepository().getPlaceImage(bookingPlaceLogo, placeInfoData.imagePath!!, this)
      FBTicketRepository().getTickets(placeInfoData.name!!, getTicketListener)
      bookingname.text = placeInfoData.name
    }
    paymentButton.setOnClickListener(this)
  }

  // 팝업 띄우는 함수
  lateinit var noticePopup: ChoicePopUp // 전역으로 선언하지 않으면 리스너에서 dismiss 사용 불가.

  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.paymentButton -> {

        if (bookingCheckBox.isChecked) {
          val temp = ticketCountSpinner.selectedItem as String
          val selectedTicketCount = temp.toInt()
          setMyTicketCount = selectedTicketCount
          val id = tickets[ticketKindSpinner.selectedItemPosition].id
          noticePopup = ChoicePopUp(this, "티켓구매",
            "티켓을 구매했습니다. \n\n[${placeInfoData.name},${ticketKindSpinner.selectedItem},${ticketDateSpinner.selectedItem} 까지, ${ticketCountSpinner.selectedItem} 개]",
            "확인", "선물하기",
            View.OnClickListener {
              for (i in 1..selectedTicketCount) {
                FBTicketRepository().getTicket(
                  placeInfoData.name!!,
                  id!!,
                  getTicketListener
                )
              }

            },
            View.OnClickListener {
              noticePopup.dismiss()
            })
          noticePopup.show()
        } else {
          Toast.makeText(this, "결제 동의를 클릭해주세요", Toast.LENGTH_LONG).show()
        }
      }
    }
  }

  val getTicketListener = object : GetTicketListener {
    override fun tickets(ticketDatas: ArrayList<TicketData>) {
      tickets = ticketDatas
      val ticketKinds = arrayListOf<String>()
      val ticketCounts = arrayListOf<String>()
      ticketDatas.forEach {
        ticketKinds.add(it.kinds!!)
      }
      for (i in 1..5) {
        ticketCounts.add(i.toString())
      }
      var arrayAdapter =
        ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, ticketKinds)
      ticketKindSpinner.adapter = arrayAdapter


      arrayAdapter =
        ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, ticketCounts)
      ticketCountSpinner.adapter = arrayAdapter

    }

    override fun myTickets(
      myTicketDatas: ArrayList<MyTicketData>,
      ticketDatas: ArrayList<TicketData>
    ) {
    }

    override fun getTicketReference(reference: DocumentReference) {
      val dt = Date()
      val myticket = MyTicketData(dt.time.toString(), reference, 0L, TicketState.UNUSED)
      FBTicketRepository().setMyTicket(myticket, setTicketListener)
    }
  }

  val setTicketListener = object : SetTicketListener {
    override fun setMyTicket() {
      setMyTicketCount--
      if (setMyTicketCount == 0) {
        startActivity(nextIntent)
        finish()
      }
    }
  }
}
