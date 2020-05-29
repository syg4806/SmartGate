package com.chambit.smartgate.ui.beacon

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chambit.smartgate.App
import com.chambit.smartgate.R
import com.chambit.smartgate.constant.Constants.CERTIFICATE_NO
import com.chambit.smartgate.dataClass.OwnedTicket
import com.chambit.smartgate.dataClass.PlaceData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.util.Logg
import kotlinx.android.synthetic.main.activity_test_b_l_e.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.altbeacon.beacon.*


class TicketUsingActivity : AppCompatActivity(), BeaconConsumer, CoroutineScope by MainScope() {
  private val multiplePermissionsCode = 100          //권한
  private val requiredPermissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
  private lateinit var ownedTicket: OwnedTicket
  lateinit var beaconManager: BeaconManager
  private lateinit var ticketData: TicketData

  // 감지된 비콘들을 임시로 담을 리스트
  var beaconList = ArrayList<Beacon>();
  var BSearching = true
  lateinit var gateArrayList: List<String>

  @RequiresApi(Build.VERSION_CODES.P)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_test_b_l_e)
    val certificateNo = intent.getLongExtra(CERTIFICATE_NO, 0L)
    checkPermissions()
    initBeaconConsumer()
    initButton()
    launch {
      ownedTicket = FBTicketRepository().getOwnedTicket(certificateNo)!!
      gateArrayList =
        ownedTicket.ticketRef!!.get().await().toObject(TicketData::class.java)!!.placeRef!!.get()
          .await().toObject(PlaceData::class.java)?.gateArray!!
      readAdvertise()
    }
  }

  private fun initButton() {
    testBTN.setOnClickListener{
      useTicket()
    }
  }

  private fun readAdvertise() {
    // 비콘 탐지를 시작한다. 실제로는 서비스를 시작하는것.
    beaconManager.bind(this)
  }

  private fun initBeaconConsumer() {
    // 실제로 비콘을 탐지하기 위한 비콘매니저 객체를 초기화
    beaconManager = BeaconManager.getInstanceForApplication(App.instance);

    // 여기가 중요한데, 기기에 따라서 setBeaconLayout 안의 내용을 바꿔줘야 하는듯 싶다.
    // 필자의 경우에는 아래처럼 하니 잘 동작했음.
    beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT))
    beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT))
    beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT))
    beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT))
    beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.URI_BEACON_LAYOUT))
  }

  override fun onDestroy() {
    super.onDestroy()
    Logg.d("destroy!!!")
    beaconManager.unbind(this@TicketUsingActivity)
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
              //권한 획득 실패
            }
          }
        }
      }
    }
  }


  override fun onBeaconServiceConnect() {
    beaconManager.addRangeNotifier { beacons, region ->
      if (BSearching) {
        Log.d("ah?", "I'm Searched!")
        if (beacons.isNotEmpty()) {
          BSearching = false
          beaconList.clear()
          beacons.forEach {
            Logg.d(
              "ID : " + it.id2 + " / " + "Distance : " + String.format(
                "%.3f",
                it.distance
              )
            )
            if (it.distance < 1&&gateArrayList.contains(it.id2.toString())) {
              useTicket()
            }
          }
        }
      }else{
        Logg.d("I'm waiting to research")
      }
    }
    try {
      beaconManager.startRangingBeaconsInRegion(
        Region(
          "myMonitoringUniqueId",
          null,
          null,
          null
        )
      )
    } catch (ignored: RemoteException) {
      Log.e("onBeaconServiceConnect", ignored.toString())
    }
  }

  private fun useTicket() {
    MainScope().launch {
      if (FBTicketRepository().useTicket(ownedTicket.certificateNo!!)) {
        finish()
      } else {
        BSearching = true
      }
    }
  }
}