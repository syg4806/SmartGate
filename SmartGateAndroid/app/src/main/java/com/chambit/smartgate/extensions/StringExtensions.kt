package com.chambit.smartgate.extensions

import android.widget.Toast
import com.chambit.smartgate.App

fun String.show() {
  Toast.makeText(App.instance.context(), this, Toast.LENGTH_SHORT).show()
}