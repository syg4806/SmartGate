package com.chambit.smartgate.ui.main.booking.placelist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.PlaceListData
import kotlinx.android.synthetic.main.activity_place_list.*

class PlaceListActivity : AppCompatActivity() {
    lateinit var infoData: PlaceListData
    lateinit var infoDatas: ArrayList<PlaceListData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_list)


        /**
         *  네트워크 패키지에서 값 할당하면 될듯.
         */
        infoData = PlaceListData()
        infoDatas = arrayListOf()

        for( i in 1..100) {
            infoData.placeLogo = "롯데월드"
            infoData.placeName = "ㅋㅋㅋㅋㅋ"
            infoDatas.add(infoData)
        }
        bookingRecyclerView.layoutManager = LinearLayoutManager(this)

        bookingRecyclerView.adapter =
            PlaceListRecyclerViewAdapter(
                infoDatas
            )
    }
}
