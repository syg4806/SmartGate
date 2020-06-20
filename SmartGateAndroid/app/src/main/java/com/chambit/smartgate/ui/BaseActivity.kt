package com.chambit.smartgate.ui

import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.util.Logg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

open class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {

  override fun onStop() {
    super.onStop()
    Logg.d("on stop")
    cancel()
  }
}