package com.chambit.smartgate.ui.main.booking.placelist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.PlaceData
import com.chambit.smartgate.network.FBPlaceRepository
import com.chambit.smartgate.ui.main.MainActivity
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.MyProgressBar
import kotlinx.android.synthetic.main.activity_place_list.*

class PlaceListActivity : AppCompatActivity() {
  lateinit var infoData: PlaceData
  lateinit var infoDatas: ArrayList<PlaceData>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_place_list)

    val progressbar = MyProgressBar(this)
    progressbar.show()

    FBPlaceRepository().listPlaces {
      if (it.isNotEmpty()) {
        //adpater 추가
        bookingRecyclerView.layoutManager = LinearLayoutManager(this)
        bookingRecyclerView.adapter = PlaceListRecyclerViewAdapter(it, this)
      }
      progressbar.dismiss()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == 100) {
      Logg.d("실행?")
      val nextIntent = Intent(this, MainActivity::class.java)
      setResult(100, nextIntent)
      finish()
    }
  }

  fun onClick(view: View) {
    when (view.id) {
      R.id.searchButton -> {

      }
    }

  }
}
