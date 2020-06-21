package com.chambit.smartgate.ui

import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.util.Logg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

open class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {
  val jobList=ArrayList<Job>()

  override fun onStop() {
    super.onStop()
    jobList.forEach { it.cancel() }
    jobList.clear()
  }
}