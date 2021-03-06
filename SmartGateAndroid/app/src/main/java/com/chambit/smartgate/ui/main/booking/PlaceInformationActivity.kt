package com.chambit.smartgate.ui.main.booking

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.constant.Constants
import com.chambit.smartgate.constant.Constants.PLACE_ID
import com.chambit.smartgate.dataClass.PlaceData
import com.chambit.smartgate.network.FBPlaceImageRepository
import com.chambit.smartgate.network.FBPlaceRepository
import com.chambit.smartgate.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_place_information.*
import kotlinx.coroutines.launch

class PlaceInformationActivity : BaseActivity() {
  lateinit var placeInfoData: PlaceData

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_place_information)

    //전달 받은 값으로 Title 설정
    val placeId = intent.getStringExtra(Constants.PLACE_ID)

    launch {
      FBPlaceRepository().getPlaceInfo(placeId!!).let {
        placeInfoData = it
        FBPlaceImageRepository().getPlaceImage(
          placeInfoMapImage,
          it.imagePath!!,
          this@PlaceInformationActivity
        )
        FBPlaceImageRepository().getPlaceLogoImage(
          placeInfoLogo,
          it.logoPath!!,
          this@PlaceInformationActivity
        )
        placeInfoMapDescription.text = placeInfoData.desc
      }
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
