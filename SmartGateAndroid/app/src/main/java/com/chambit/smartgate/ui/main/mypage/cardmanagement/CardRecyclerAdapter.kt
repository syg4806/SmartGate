package com.chambit.smartgate.ui.main.mypage.cardmanagement

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chambit.smartgate.App
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.OwnedTicket
import com.chambit.smartgate.dataClass.PlaceData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.network.BaseFB
import com.chambit.smartgate.network.FBPlaceRepository
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.ui.send.SendTicketActivity
import kotlinx.android.synthetic.main.myticket_recycler_item.view.*
import kotlinx.coroutines.*

class CardRecyclerAdapter(val context: Context, val ownedTickets: MutableList<OwnedTicket>) :
  RecyclerView.Adapter<CardRecyclerAdapter.mViewHolder>(), CoroutineScope by MainScope() {

  //생성된 뷰 홀더에 데이터를 바인딩 해줌.
  override fun onBindViewHolder(holder: mViewHolder, position: Int) {
    val ownedTicket = ownedTickets[position]
    launch {
      var ticketData: TicketData? = null
      var placeData: PlaceData? = null
      var imgUri: Uri? = null
      withContext(Dispatchers.IO) {
        ticketData = FBTicketRepository().getTicket(ownedTicket.ticketRef!!).also {
          placeData = FBPlaceRepository().getPlace(it.placeRef!!)
          imgUri = BaseFB().getImage(it.imagePath!!)
        }
      }
      Glide.with(App.instance)
        .load(imgUri)
        .override(1024, 980)
        .into(holder.imageView)
      holder.place.text = placeData?.name
      holder.kinds.text = ticketData?.kinds
      holder.date.text = ownedTicket.expirationDate.toString()
    }
    holder.giftButton.setOnClickListener {
      // TODO: 선물하기 화면으로 이동
      val nextIntent = Intent(context, SendTicketActivity::class.java)
      nextIntent.putExtra("certificateNo", ownedTicket.certificateNo) //nickname 정보 인텐트로 넘김
      //nextIntent.putExtra("nickname", holder.nickname.text.toString())
      context.startActivity(nextIntent)
    }
  }

  //뷰 홀더 생성
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
    val view =
      LayoutInflater.from(parent.context).inflate(R.layout.card_recycler_item, parent, false)
    return mViewHolder(view) //view 객체는 한개의 리사이클러뷰가 디자인 되어 있는 레이아웃을 의미
  }

  //item 사이즈, 데이터의 전체 길이 반환
  override fun getItemCount(): Int {
    return ownedTickets.size
  }

  inner class mViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var imageView = view.myTicketItemImageView
    var place = view.myTicketItemPlaceTextView
    var kinds = view.myTicketItemKindsTextView
    var date = view.myTicketItemDateTextView
    var giftButton = view.myTicketActivityItemGiftButton
  }
}

