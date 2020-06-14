package com.chambit.smartgate.ui.main.booking.placelist

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.PlaceData
import com.chambit.smartgate.extension.show
import com.chambit.smartgate.network.FBPlaceRepository
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.MyProgressBar
import kotlinx.android.synthetic.main.activity_place_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PlaceListActivity : AppCompatActivity(), CoroutineScope by MainScope() {
  lateinit var infoData: PlaceData
  lateinit var infoDatas: ArrayList<PlaceData>
  val activity = this

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_place_list)

    val progressbar = MyProgressBar(this)
    progressbar.show()

    FBPlaceRepository().listPlaces {
      if (it.isNotEmpty()) {
        //adpater 추가
        bookingRecyclerView.layoutManager = LinearLayoutManager(activity)
        bookingRecyclerView.adapter = PlaceListRecyclerViewAdapter(it, activity)
      }
      progressbar.dismiss()
    }
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.searchButton -> {

        launch {
          FBPlaceRepository().searchPlace(editText.text.toString())?.let {
            bookingRecyclerView.adapter = PlaceListRecyclerViewAdapter(it, activity)
          }?:let {
            "해당 검색 결과가 없습니다.".show()
          }
        }
      }
    }

  }
}
