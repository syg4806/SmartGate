package com.chambit.smartgate.ui.main.mypage.cardmanagement

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.chambit.smartgate.R
import com.chambit.smartgate.dataClass.CardData
import com.chambit.smartgate.extensions.longToast
import com.chambit.smartgate.extensions.shortToast
import com.chambit.smartgate.network.FBUsersRepository
import com.chambit.smartgate.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_card_add.*
import kotlinx.coroutines.launch

class CardAddActivity : BaseActivity(), View.OnClickListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_card_add)

    card_add_button.setOnClickListener(this)
  }

  override fun onClick(v: View?) {
    when (v!!.id) {
      R.id.card_add_button -> {
        // 카드 정보가 제대로 입력이 안되었을 때
        if (card_number_EditText.text.isEmpty() || card_Validity.text.isEmpty() || card_CVC.text.isEmpty() || card_add_name.text.isEmpty() || birth_EditText.text.isEmpty()) {
          this.shortToast("카드 정보를 모두 입력해주세요.")
          return
        }
        card_add_button.isEnabled = false
        val cardData = CardData(
          card_number_EditText.text.toString(),
          card_Validity.text.toString(),
          card_CVC.text.toString(),
          card_add_name.text.toString(),
          birth_EditText.text.toString()
        )
        launch {
          FBUsersRepository().setUserCard(cardData).let {
            this@CardAddActivity.longToast("카드 추가가 완료되었습니다.")
            finish()
          }
        }
      }
    }
  }
}
