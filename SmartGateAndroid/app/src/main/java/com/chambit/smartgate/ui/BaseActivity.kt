package com.chambit.smartgate.ui

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

open class BaseActivity: AppCompatActivity(), CoroutineScope by MainScope() {
}