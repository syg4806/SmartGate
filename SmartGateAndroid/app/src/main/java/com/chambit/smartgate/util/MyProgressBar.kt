package com.chambit.smartgate.util

import android.content.Context

class MyProgressBar(context: Context) {
  var switch = 0
  var mprogressBar = ProgressBar(context)

  fun show() {
    if (switch == 0) mprogressBar.show()
    switch++
  }

  fun dismiss() {
    switch--
    if (switch == 0) mprogressBar.dismiss()
  }
}