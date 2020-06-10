package com.chambit.smartgate.ui.send

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chambit.smartgate.R
import com.chambit.smartgate.util.Logg
import com.kakao.friends.response.model.AppFriendInfo
import kotlinx.android.synthetic.main.friend_item.view.*

class FriendListRecyclerViewAdapter(val friendList: ArrayList<AppFriendInfo>, val activity: Activity) :
  RecyclerView.Adapter<FriendListRecyclerViewAdapter.mViewHolder>() {


  var context: Context? = null // 부모 context
  var uuids : ArrayList<String>? = null

  //생성된 뷰 홀더에 데이터를 바인딩 해줌.
  override fun onBindViewHolder(holder: mViewHolder, position: Int) {
    val friend = friendList[position]

    holder.friendNameTextView.text = friend.profileNickname
    val uuid = friend.uuid // 메시지 전송 시 사용
    Logg.d("나와라~${friend.uuid}")
//    uuids!!.add(uuid)
    Logg.d(uuids!!.size.toString())
    holder.click.setOnClickListener {
      Logg.d(friendList[position].uuid)
      Logg.d(friendList[position].profileNickname)
//      KakaoTalkService.getInstance().sendMessageToFriends(uuids!!, "null", object : TalkResponseCallback<MessageSendResponse>(){
//        override fun onNotKakaoTalkUser() {
//          TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onSessionClosed(errorResult: ErrorResult?) {
//          TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onFailure(errorResult: ErrorResult?) {
//          super.onFailure(errorResult)
//        }
//
//        override fun onSuccess(result: MessageSendResponse?) {
//          if (result!!.successfulReceiverUuids() != null) {
//            Logg.i("친구에게 보내기 성공");
//            Logg.d( "전송에 성공한 대상: " + result.successfulReceiverUuids());
//          }
//        }
//      })
    }
//    holder.friendNameTextView.text = friends
  }

  //뷰 홀더 생성
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.friend_item, parent, false)
    context = parent.context
    return mViewHolder(view) //view 객체는 한개의 리사이클러뷰가 디자인 되어 있는 레이아웃을 의미
  }

  //item 사이즈, 데이터의 전체 길이 반ㅎ환
  override fun getItemCount(): Int {
    return friendList.size
  }

  //여기서 item을 textView에 옮겨줌
  inner class mViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var friendNameTextView = view.friendNameTextView
    var click = view.textClick
  }
}