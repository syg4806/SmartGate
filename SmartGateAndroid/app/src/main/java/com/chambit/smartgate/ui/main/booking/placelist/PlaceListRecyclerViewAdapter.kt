package com.chambit.smartgate.ui.main.booking.placelist

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.PlaceListData
import com.chambit.smartgate.ui.main.booking.PlaceInformationActivity
import kotlinx.android.synthetic.main.recycler_booking_activity_item.view.*

class PlaceListRecyclerViewAdapter(val mdata: ArrayList<PlaceListData>) :
    RecyclerView.Adapter<PlaceListRecyclerViewAdapter.mViewHolder>() {

    var context: Context? = null // 부모 context

    //생성된 뷰 홀더에 데이터를 바인딩 해줌.
    override fun onBindViewHolder(holder: mViewHolder, position: Int) {
        val singleItem = mdata[position]

        holder.placeLogo.text = singleItem.placeLogo
        holder.placeName.text = singleItem.placeName


        // 클릭하면
        holder.itemView.setOnClickListener {
            val nextIntent = Intent(context, PlaceInformationActivity::class.java)
            context!!.startActivity(nextIntent)
        }
    }

    //뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_booking_activity_item, parent, false)
        context = parent.context
        return mViewHolder(view) //view 객체는 한개의 리사이클러뷰가 디자인 되어 있는 레이아웃을 의미
    }

    //item 사이즈, 데이터의 전체 길이 반ㅎ환
    override fun getItemCount(): Int {
        return mdata.size
    }

    //여기서 item을 textView에 옮겨줌
    inner class mViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var placeLogo =view.placeLogo
        var placeName = view.placeName
    }

}