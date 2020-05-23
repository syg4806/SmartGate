package com.chambit.smartgate.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.chambit.smartgate.R
import kotlinx.android.synthetic.main.choice_pop_up.*

class ChoicePopUp(
  context: Context,
  private val bodyText: String,
  private val yes: View.OnClickListener,
  private val no: View.OnClickListener
) : Dialog(context) {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.choice_pop_up)

   window!!.setBackgroundDrawable(ColorDrawable (Color.TRANSPARENT))
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
    bodyTextView.text = bodyText
    yesButton.setOnClickListener(yes)
    noButton.setOnClickListener(no)
  }
}
