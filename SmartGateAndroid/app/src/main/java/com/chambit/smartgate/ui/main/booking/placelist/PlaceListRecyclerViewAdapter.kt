package com.chambit.smartgate.ui.main.booking.placelist

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.R
import com.chambit.smartgate.constant.Constants
import com.chambit.smartgate.dataClass.PlaceData
import com.chambit.smartgate.network.FBPlaceImageRepository
import com.chambit.smartgate.ui.main.booking.PlaceInformationActivity
import kotlinx.android.synthetic.main.recycler_booking_activity_item.view.*

class PlaceListRecyclerViewAdapter(val placeList: ArrayList<PlaceData>, val activity: Activity) :
  RecyclerView.Adapter<PlaceListRecyclerViewAdapter.mViewHolder>() {

  //생성된 뷰 홀더에 데이터를 바인딩 해줌.
  override fun onBindViewHolder(holder: mViewHolder, position: Int) {
    val place = placeList[position]

    FBPlaceImageRepository().getPlaceLogoImage(holder.placeLogo, place.logoPath!!, activity)
    holder.name.text = place.name


    // 클릭하면
    holder.itemView.setOnClickListener {
//      it.background = AppCompatResources.getDrawable(context!! ,R.drawable.ic_place_item_pressed)
      val nextIntent = Intent(activity, PlaceInformationActivity::class.java)
      nextIntent.putExtra(Constants.PLACE_ID, place.id)
      activity.startActivityForResult(nextIntent,101)
    }
  }

  //뷰 홀더 생성
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.recycler_booking_activity_item, parent, false)
    return mViewHolder(view) //view 객체는 한개의 리사이클러뷰가 디자인 되어 있는 레이아웃을 의미
  }

  //item 사이즈, 데이터의 전체 길이 반ㅎ환
  override fun getItemCount(): Int {
    return placeList.size
  }

  //여기서 item을 textView에 옮겨줌
  inner class mViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var placeLogo = view.placeLogo
    var name = view.name
  }

}