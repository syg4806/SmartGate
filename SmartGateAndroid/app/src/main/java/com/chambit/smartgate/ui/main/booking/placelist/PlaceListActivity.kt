package com.chambit.smartgate.ui.main.booking.placelist

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chambit.smartgate.R
import com.chambit.smartgate.network.FBPlaceRepository
import com.chambit.smartgate.util.MyProgressBar
import kotlinx.android.synthetic.main.activity_place_list.*

class PlaceListActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_place_list)

    val progressbar = MyProgressBar(this)
    progressbar.show()

    FBPlaceRepository().listPlaces {
      if (it.isNotEmpty()) {
        //adpater 추가
        bookingRecyclerView.layoutManager = LinearLayoutManager(this@PlaceListActivity)
        bookingRecyclerView.adapter = PlaceListRecyclerViewAdapter(this@PlaceListActivity, it)
      }
      progressbar.dismiss()
    }
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.searchButton -> {
        //TODO 작업 시작 필요
      }
    }

  }
}
