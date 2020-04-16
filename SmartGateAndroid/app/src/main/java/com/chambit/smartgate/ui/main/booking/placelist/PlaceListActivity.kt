package com.chambit.smartgate.ui.main.booking.placelist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.MyTicketData
import com.chambit.smartgate.dataClass.PlaceInfoData
import com.chambit.smartgate.dataClass.PlaceListData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.network.FBPlaceRepository
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.network.GetPlaceListener
import com.chambit.smartgate.network.GetTicketListener
import com.chambit.smartgate.ui.main.myticket.MyTicketRecyclerAdapter
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.MyProgressBar
import kotlinx.android.synthetic.main.activity_my_ticket.*
import kotlinx.android.synthetic.main.activity_place_list.*

class PlaceListActivity : AppCompatActivity() {
  lateinit var infoData: PlaceListData
  lateinit var infoDatas: ArrayList<PlaceListData>
  val activity = this

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_place_list)

    val progressbar =  MyProgressBar(this)
    progressbar.show()

    val getPlaceListener = object : GetPlaceListener {
      override fun getPlaceList(placeListDatas: ArrayList<PlaceListData>) {
        if (placeListDatas.isNotEmpty()) {

          //adpater 추가
          bookingRecyclerView.layoutManager =
            LinearLayoutManager(activity)
          bookingRecyclerView.adapter =
            PlaceListRecyclerViewAdapter(placeListDatas, activity)
        }
        progressbar.dismiss()
      }

      override fun getPlaceInfo(placeInfoData: PlaceInfoData) {
      }
    }
    FBPlaceRepository().getPlaceList(getPlaceListener)
  }
}
