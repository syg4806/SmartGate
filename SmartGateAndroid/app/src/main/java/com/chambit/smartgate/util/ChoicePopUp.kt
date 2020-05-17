package com.chambit.smartgate.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.chambit.smartgate.R
import kotlinx.android.synthetic.main.choice_pop_up.*

class ChoicePopUp(context: Context,
                  private val titleText:String,
                  private val bodyText:String,
                  private val yesText:String,
                  private val noText:String,
                  private val yes: View.OnClickListener,
                  private val no: View.OnClickListener) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choice_pop_up)

//        requestWindowFeature(Window.FEATURE_NO_TITLE)
        titleTextView.text = titleText
        bodyTextView.text=bodyText
        yesButton.text = yesText
        noButton.text = noText
        yesButton.setOnClickListener(yes)
        noButton.setOnClickListener(no)
    }
}
