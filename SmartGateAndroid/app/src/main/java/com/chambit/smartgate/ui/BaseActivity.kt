package com.chambit.smartgate.ui

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope

open class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {
  val jobList=ArrayList<Job>()

  override fun onStop() {
    super.onStop()
    jobList.forEach { it.cancel() }
    jobList.clear()
  }
}