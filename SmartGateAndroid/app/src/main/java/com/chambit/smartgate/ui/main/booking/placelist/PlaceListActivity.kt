package com.chambit.smartgate.ui.main.booking.placelist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.PlaceInfoData
import com.chambit.smartgate.network.FBPlaceRepository
import com.chambit.smartgate.network.GetPlaceListener
import com.chambit.smartgate.util.MyProgressBar
import kotlinx.android.synthetic.main.activity_place_list.*

class PlaceListActivity : AppCompatActivity() {
  lateinit var infoData: PlaceInfoData
  lateinit var infoDatas: ArrayList<PlaceInfoData>
  val activity = this

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_place_list)

    val progressbar = MyProgressBar(this)
    progressbar.show()

    FBPlaceRepository().listPlaces{
      if (it.isNotEmpty()) {
        //adpater 추가
        bookingRecyclerView.layoutManager = LinearLayoutManager(activity)
        bookingRecyclerView.adapter = PlaceListRecyclerViewAdapter(it, activity)
      }
      progressbar.dismiss()
    }
  }
}
