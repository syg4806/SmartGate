package com.chambit.smartgate.ui.main.myticket

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.MyTicketData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.network.FBTicketImage
import com.chambit.smartgate.ui.send.SendTicketActivity
import com.chambit.smartgate.util.Logg
import kotlinx.android.synthetic.main.myticket_recycler_item.view.*


class MyTicketRecyclerAdapter(val mdata: ArrayList<MyTicketData>, val activity: Activity) :
  RecyclerView.Adapter<MyTicketRecyclerAdapter.mViewHolder>() {
  var context: Context? = null
  val GETLIKES = 50

  //생성된 뷰 홀더에 데이터를 바인딩 해줌.
  override fun onBindViewHolder(holder: mViewHolder, position: Int) {
    val singleItem = mdata[position]

    //FBTicketImage().getTicketImage(holder.imageView, ticketItem.certificateNo!!, activity)
    holder.place.text ="레퍼런스가 아니라 장소이름도 가져와야함"
    //holder.place.text = ticketItem.
    holder.kinds.text = "레퍼런스에서 가져와서 종류 적용"
    holder.date.text = singleItem.expirationDate.toString()

    holder.giftButton.setOnClickListener {
      // TODO: 선물하기 화면으로 이동
      val nextIntent = Intent(context, SendTicketActivity::class.java)
      /* nextIntent.putExtra("nickname", mapTitle) //nickname 정보 인텐트로 넘김
       nextIntent.putExtra("nickname", holder.nickname.text.toString())*/
      context!!.startActivity(nextIntent)
    }
  }

  //뷰 홀더 생성
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
    val view =
      LayoutInflater.from(parent.context).inflate(R.layout.myticket_recycler_item, parent, false)
    context = parent.context
    return mViewHolder(view) //view 객체는 한개의 리사이클러뷰가 디자인 되어 있는 레이아웃을 의미
  }

  //item 사이즈, 데이터의 전체 길이 반환
  override fun getItemCount(): Int {
    return mdata.size
  }

  inner class mViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var imageView = view.myTicketItemImageView
    var place = view.myTicketItemPlaceTextView
    var kinds = view.myTicketItemKindsTextView
    var date = view.myTicketItemDateTextView
    var giftButton = view.myTicketActivityItemGiftButton
  }


}

