package com.chambit.smartgate.ui.send

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.KakaoFriendInfo
import com.chambit.smartgate.util.Logg
import kotlinx.android.synthetic.main.friend_item.view.*

class FriendListRecyclerViewAdapter(
  val friendList: ArrayList<KakaoFriendInfo>,
  val activity: Activity
) :
  RecyclerView.Adapter<FriendListRecyclerViewAdapter.mViewHolder>() {

  var context: Context? = null // 부모 context
  var uuids: ArrayList<String>? = null
  var selectFlag : Boolean? = null


  //생성된 뷰 홀더에 데이터를 바인딩 해줌.
  override fun onBindViewHolder(holder: mViewHolder, position: Int) {
    val friend = friendList[position]

    holder.friendNameTextView.text = friend.friendInfo!!.profileNickname
    val uuid = friend.friendInfo!!.uuid // 메시지 전송 시 사용
    Logg.d("나와라~")

    if(friend.selectFlag){
      holder.friendCheckBox.setImageResource(R.drawable.ic_friend_checked)
      Logg.d("유유아이디 : ${friend.friendInfo!!.profileNickname}")
    }else{
      holder.friendCheckBox.setImageResource(R.drawable.ic_friend_unchecked)
    }

    holder.friendCheckBox.setOnClickListener {

      friend.selectFlag = !friend.selectFlag
      notifyDataSetChanged() // onBindViewHolder recall
    }
    holder.friendClick.setOnClickListener {

      friend.selectFlag = !friend.selectFlag
      notifyDataSetChanged()
    }
//    holder.friendNameTextView.text = friends
  }

  //뷰 홀더 생성
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.friend_item, parent, false)
    context =activity
    return mViewHolder(view) //view 객체는 한개의 리사이클러뷰가 디자인 되어 있는 레이아웃을 의미
  }

  //item 사이즈, 데이터의 전체 길이 반ㅎ환
  override fun getItemCount(): Int {
    return friendList.size
  }

  //여기서 item을 textView에 옮겨줌
  inner class mViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var friendNameTextView = view.friendNameTextView
    var friendClick = view.friendClick
    val friendCheckBox = view.friendCheckBox
  }
}