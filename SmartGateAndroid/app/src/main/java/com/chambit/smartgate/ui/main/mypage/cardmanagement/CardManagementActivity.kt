package com.chambit.smartgate.ui.main.mypage.cardmanagement

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.chambit.smartgate.R
import com.chambit.smartgate.extensions.gone
import com.chambit.smartgate.extensions.visible
import com.chambit.smartgate.network.FBUsersRepository
import com.chambit.smartgate.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_card_management.*
import kotlinx.coroutines.launch

class CardManagementActivity : BaseActivity(), View.OnClickListener {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_card_management)

    cardImageView.setOnClickListener(this)
    cardDeleteButton.setOnClickListener(this)
  }

  override fun onResume() {
    super.onResume()

    launch {
      FBUsersRepository().getUserCard()?.let {
        cardImageView.isEnabled = false
        cardImageView.visible()
        cardDeleteButton.visible()

        cardImageView.setImageResource(R.drawable.ic_card_default_image)
        cardNumberTextView.text = it.number
        cardCVCTextView.text = it.cvc
        cardNameTextView.text = it.name
        cardValidityTextView.text = it.validity
      } ?: run {
        cardImageView.isEnabled = true
        cardImageView.setImageResource(R.drawable.ic_card_add)
        cardDeleteButton.gone()
        cardImageView.visible()
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
        cardDeleteButton.gone()
        cardNumberTextView.text = ""
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
