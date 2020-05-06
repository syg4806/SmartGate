package com.chambit.smartgate.ui.main.booking

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.constant.Constants
import com.chambit.smartgate.constant.Constants.PLACE_ID
import com.chambit.smartgate.dataClass.PlaceInfoData
import com.chambit.smartgate.network.FBPlaceImageRepository
import com.chambit.smartgate.network.FBPlaceRepository
import kotlinx.android.synthetic.main.activity_place_information.*

class PlaceInformationActivity : AppCompatActivity() {
  val activity = this
  lateinit var placeInfoData: PlaceInfoData

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_place_information)

    val intent = intent
    //전달 받은 값으로 Title 설정
    val id = intent.getStringExtra(Constants.PLACE_ID)

    FBPlaceRepository().getPlaceInfo(id) {
      placeInfoData = it
      FBPlaceImageRepository().getPlaceImage(
        placeInfoMapImage,
        placeInfoData.imagePath!!,
        activity
      )
      FBPlaceImageRepository().getPlaceLogoImage(
        placeInfoLogo,
        placeInfoData.logoPath!!,
        activity
      )
      placeInfoMapDescription.text = placeInfoData.desc
    }

  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.toReserveButton -> {
        val nextIntent = Intent(this, BookingActivity::class.java)
        nextIntent.putExtra(PLACE_ID, placeInfoData.id)
        startActivity(nextIntent)
      }
    }
  }
}
