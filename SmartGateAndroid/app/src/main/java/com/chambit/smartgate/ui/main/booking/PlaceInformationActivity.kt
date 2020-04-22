package com.chambit.smartgate.ui.main.booking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.PlaceInfoData
import com.chambit.smartgate.dataClass.PlaceListData
import com.chambit.smartgate.network.FBPlaceImageRepository
import com.chambit.smartgate.network.FBPlaceRepository
import com.chambit.smartgate.network.GetPlaceListener
import com.chambit.smartgate.ui.main.booking.placelist.PlaceListRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_place_information.*
import kotlinx.android.synthetic.main.activity_place_list.*

class PlaceInformationActivity : AppCompatActivity() {
  val activity = this
  var activityPlaceInfoData = PlaceInfoData()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_place_information)

    val intent = intent
    //전달 받은 값으로 Title 설정
    val placeName = intent.extras?.getString("placeName").toString()
    FBPlaceRepository().getPlaceInfo(placeName, getPlaceListener)

  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.toReserveButton -> {
        val nextIntent = Intent(this, BookingActivity::class.java)
        nextIntent.putExtra("PlaceInfoData", activityPlaceInfoData)
        startActivity(nextIntent)
      }
    }
  }

  val getPlaceListener = object : GetPlaceListener {
    override fun getPlaceList(placeListDatas: ArrayList<PlaceListData>) {
    }

    override fun getPlaceInfo(placeInfoData: PlaceInfoData) {
      FBPlaceImageRepository().getPlaceImage(
        placeInfoMapImage,
        placeInfoData.placeImagePath!!,
        activity
      )
      FBPlaceImageRepository().getPlaceLogoImage(
        placeInfoLogo,
        placeInfoData.placeLogoPath!!,
        activity
      )
      placeInfoMapDescription.text = placeInfoData.discription
      activityPlaceInfoData = placeInfoData
    }
  }
}
