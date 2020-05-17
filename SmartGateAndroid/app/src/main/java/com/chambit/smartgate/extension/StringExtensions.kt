package com.chambit.smartgate.extension

import android.widget.Toast
import com.chambit.smartgate.App

fun String.show() {
  Toast.makeText(App.instance.context(), this, Toast.LENGTH_SHORT).show()
}