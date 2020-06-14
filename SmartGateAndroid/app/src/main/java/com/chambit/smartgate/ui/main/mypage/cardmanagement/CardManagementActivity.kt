package com.chambit.smartgate.ui.main.mypage.cardmanagement

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.CardData
import com.chambit.smartgate.network.FBUsersRepository
import com.chambit.smartgate.ui.main.myticket.MyTicketActivity
import com.chambit.smartgate.ui.main.myticket.MyTicketRecyclerAdapter
import com.chambit.smartgate.util.Logg
import kotlinx.android.synthetic.main.activity_card_management.*
import kotlinx.android.synthetic.main.activity_used_ticket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CardManagementActivity : AppCompatActivity(), View.OnClickListener, CoroutineScope by MainScope() {
  lateinit var nextIntent: Intent


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_card_management)

    nextIntent = Intent(this, CardAddActivity::class.java)
    cardImageView.setOnClickListener(this)
    cardDeleteButton.setOnClickListener(this)
  }

  override fun onResume() {
    super.onResume()

    launch {
      FBUsersRepository().getUserCard().let {
        if (it != null) {
          //TODO 살짝 비동기 문제인지 등록하자마자 VIEW에 뜨는게 느릴때가 있어서 밀리는 경우가 있는데.. 이걸 어떻게 해야할까요..
          cardImageView.isEnabled = false
          cardImageView.visibility = View.VISIBLE
          cardDeleteButton.visibility = View.VISIBLE

          cardImageView.setImageResource(R.drawable.ic_card_default_image)
          cardNumberTextView.text = it.number
          cardCVCTextView.text = it.cvc
          cardNameTextView.text = it.name
          cardValidityTextView.text = it.validity
        }
        else {
          cardImageView.isEnabled = true
          cardImageView.setImageResource(R.drawable.ic_card_add)
          cardDeleteButton.visibility = View.GONE
          cardImageView.visibility = View.VISIBLE
        }
      }
    }
  }

  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.cardImageView -> {
        startActivity(Intent(this@CardManagementActivity, CardAddActivity::class.java))
      }
      R.id.cardDeleteButton -> {
        cardImageView.isEnabled = true
        cardImageView.setImageResource(R.drawable.ic_card_add)
        cardDeleteButton.visibility = View.GONE
        cardNumberTextView.visibility = View.GONE
        cardCVCTextView.text = ""
        cardNameTextView.text = ""
        cardValidityTextView.text = ""
        launch {
          FBUsersRepository().deleteUserCard()
        }
      }
    }
  }
}
