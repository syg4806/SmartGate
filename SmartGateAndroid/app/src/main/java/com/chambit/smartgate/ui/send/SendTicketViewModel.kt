package com.chambit.smartgate.ui.send

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData


class SendTicketViewModel(application: Application) : AndroidViewModel(application) {
  var counter = MutableLiveData<Int>()

  fun CounterViewModel(ticketCount : Int?) {
    counter.value = ticketCount
  }

  fun increase() {
    counter.value = counter.value!! + 1
  }

  fun decrease() {
    counter.value = counter.value!! - 1
  }


}