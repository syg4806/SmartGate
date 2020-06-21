package com.chambit.smartgate.ui.beacon

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chambit.smartgate.App
import com.chambit.smartgate.R
import com.chambit.smartgate.constant.Constants.CERTIFICATE_NO
import com.chambit.smartgate.dataClass.OwnedTicket
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.extensions.longToast
import com.chambit.smartgate.extensions.shortToast
import com.chambit.smartgate.network.BaseFB
import com.chambit.smartgate.network.FBPlaceRepository
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.ui.BaseActivity
import com.chambit.smartgate.ui.main.mypage.usedticketlookup.UsedTicketActivity
import com.chambit.smartgate.ui.main.mypage.usedticketlookup.UsedTicketActivity.Companion.USETICKET
import com.chambit.smartgate.ui.main.myticket.MyTicketActivity
import com.chambit.smartgate.ui.main.myticket.MyTicketActivity.Companion.RECALL
import com.chambit.smartgate.util.Logg
import kotlinx.android.synthetic.main.activity_ticket_using.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region
import java.net.HttpURLConnection
import java.net.URL


class TicketUsingActivity : BaseActivity(), BeaconConsumer {
  companion object {
    //TODO : Test용 1m입니다. 후에 다시 0.5로 변경필요
    const val BOUNDARY = 1.0 //0.5m
    const val TICKET_USING_TIME = 60
    const val GATE_REGION_ID = "gateSearching"
  }

  private val multiplePermissionsCode = 100          //권한
  private val requiredPermissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
  private lateinit var ownedTicket: OwnedTicket
  private lateinit var beaconManager: BeaconManager
  private var searchFlag = true
  private lateinit var gateArrayList: List<String>

  @RequiresApi(Build.VERSION_CODES.P)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_ticket_using)
    val certificateNo = intent.getLongExtra(CERTIFICATE_NO, 0L)
    checkPermissions()
    initBeaconConsumer()
    Glide.with(this).load(R.drawable.ic_wave).fitCenter().into(usingTicketBackground)
    launch {
      ownedTicket = FBTicketRepository().getOwnedTicket(certificateNo)!!
      gateArrayList = FBPlaceRepository().listGates(ownedTicket.ticketRef!!)
      readAdvertise()
    }.let {
      jobList.add(it)
    }



    launch {
      var timer = TICKET_USING_TIME
      while (timer > 0) {
        usingTicketCountTV.text = timer.toString()
        timer--
        delay(1000)
      }
      finishUsing(false)
    }
  }

  private fun finishUsing(isSucceed: Boolean) {
    if (isSucceed) {
      startActivity(Intent(this, UsedTicketActivity::class.java).apply {
        putExtra(USETICKET, true)
      })
    } else {
      startActivity(Intent(this, MyTicketActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra(RECALL, true)
      })
    }
    finish()
  }

  private fun readAdvertise() {
    // 비콘 탐지 서비스를 액티비티에 바인드
    val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    if (mBluetoothAdapter.isEnabled) {
      beaconManager.bind(this)
    } else {
      this.longToast("블루투스 상태를 확인해주세요")
      finish()
    }
  }

  private fun initBeaconConsumer() {
    beaconManager = BeaconManager.getInstanceForApplication(App.instance);
    beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT))
  }

  private fun checkPermissions() {
    val rejectedPermissionList = ArrayList<String>()
    //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
    for (permission in requiredPermissions) {
      if (ContextCompat.checkSelfPermission(
          this,
          permission
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        //만약 권한이 없다면 rejectedPermissionList에 추가
        rejectedPermissionList.add(permission)
      }
    }
    //거절된 퍼미션이 있다면...
    if (rejectedPermissionList.isNotEmpty()) {
      //권한 요청!
      val array = arrayOfNulls<String>(rejectedPermissionList.size)
      ActivityCompat.requestPermissions(
        this,
        rejectedPermissionList.toArray(array),
        multiplePermissionsCode
      )
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      multiplePermissionsCode -> {
        if (grantResults.isNotEmpty()) {
          for ((i, permission) in permissions.withIndex()) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
              this.longToast("권한이 없으면 티켓을 사용할 수 없습니다.")
              finish()
            }
          }
        }
      }
    }
  }


  override fun onBeaconServiceConnect() {
    beaconManager.addRangeNotifier { beacons, region ->
      if (searchFlag) {
        if (beacons.isNotEmpty()) {
          searchFlag = false
          beacons.forEach {
            Logg.d(
              "ID : " + it.id2 + " / " + "Distance : " + String.format(
                "%.3f",
                it.distance
              )
            )
            if (gateArrayList.contains(it.id2.toString())) {
              if (it.distance < BOUNDARY) {
                useTicket(it.id2.toString())
              } else {
                searchFlag = true
              }
            } else {
              this.longToast("게이트에 가까이 이동해주세요.")
              searchFlag = true
            }
          }
        }
      } else {
        Logg.d("waiting to research")
      }
    }
    try {
      beaconManager.startRangingBeaconsInRegion(
        Region(
          GATE_REGION_ID,
          null,
          null,
          null
        )
      )
    } catch (ignored: RemoteException) {
      Logg.e("$ignored")
    }
  }

  override fun onStop() {
    super.onStop()
    beaconManager.unbind(this)
  }

  private fun useTicket(gateId: String) {
    launch {
      //Ticket 사용 요청
      if (!FBTicketRepository().useTicket(ownedTicket.certificateNo!!)) {
        this@TicketUsingActivity.longToast("티켓 사용에 실패했습니다.")
        searchFlag = true
      }
      //TODO : Shared에서 유저 이름 가져오기
      val userName = "TEST"
      CoroutineScope(Dispatchers.IO).launch {
        val gateIp = FBPlaceRepository().getGateIp(gateId)
        val url = URL("http://" + gateIp + "/entergate/?name=${userName}")
        Logg.d("request opening to $url")
        val myConnection = url.openConnection() as HttpURLConnection
        if (myConnection.responseCode == 200) {
          Logg.d("success to communicate with beacon")
          finishUsing(true)
        } else {
          Logg.e("error : ${myConnection.responseMessage}")
        }
      }
      this@TicketUsingActivity.shortToast("티켓을 사용 중 입니다.")
    }
  }
}