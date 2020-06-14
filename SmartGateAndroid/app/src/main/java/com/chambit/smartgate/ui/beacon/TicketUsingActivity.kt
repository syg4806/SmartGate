package com.chambit.smartgate.ui.beacon

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.chambit.smartgate.App
import com.chambit.smartgate.R
import com.chambit.smartgate.constant.Constants.CERTIFICATE_NO
import com.chambit.smartgate.dataClass.OwnedTicket
import com.chambit.smartgate.dataClass.PlaceData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.extensions.toast
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.ui.BaseActivity
import com.chambit.smartgate.ui.main.mypage.usedticketlookup.UsedTicketActivity
import com.chambit.smartgate.ui.main.mypage.usedticketlookup.UsedTicketActivity.Companion.USETICKET
import com.chambit.smartgate.ui.main.myticket.MyTicketActivity
import com.chambit.smartgate.ui.main.myticket.MyTicketActivity.Companion.RECALL
import com.chambit.smartgate.util.Logg
import kotlinx.android.synthetic.main.activity_ticket_using.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class TicketUsingActivity : BaseActivity(), BeaconConsumer {
  companion object {
    const val BOUNDARY = 0.5 //0.5m
  }

  private val multiplePermissionsCode = 100          //권한
  private val requiredPermissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
  private lateinit var ownedTicket: OwnedTicket
  private lateinit var beaconManager: BeaconManager
  private var BSearching = true
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
      gateArrayList =
        ownedTicket.ticketRef!!.get().await().toObject(TicketData::class.java)!!.placeRef!!.get()
          .await().toObject(PlaceData::class.java)?.gateArray!!
      readAdvertise()
    }
    launch {
      var timer=60
      while(timer>0) {
        usingTicketCountTV.text = timer.toString()
        timer--
        delay(1000)
      }
      finishUsing(false)
    }
  }

  private fun finishUsing(isSucceed:Boolean) {
    if(isSucceed){
      startActivity(Intent(this,UsedTicketActivity::class.java).apply {
        putExtra(USETICKET,true)
      })
      finish()
    }else{
      startActivity(Intent(this,MyTicketActivity::class.java).apply {
        flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra(RECALL,true)
      })
      finish()
    }
  }

  private fun readAdvertise() {
    // 비콘 탐지를 시작한다. 실제로는 서비스를 시작하는것.
    beaconManager.bind(this)
  }

  private fun initBeaconConsumer() {
    beaconManager = BeaconManager.getInstanceForApplication(App.instance);
    beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT))
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
        Log.d("ah?", "I'm Searching")
        if (beacons.isNotEmpty()) {
          BSearching = false
          logView.text = beacons.joinToString {
            "ID : " + it.id2 + " / " + "Distance : " + String.format("%.3f", it.distance)
          }
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
                BSearching = true
              }
            } else {
              this.toast("게이트에 가까이 이동해주세요.")
              BSearching = true
            }
          }
        }
      } else {
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

  override fun onStop() {
    super.onStop()
    beaconManager.unbind(this)
  }

  private fun useTicket(gateId: String) {
    MainScope().launch {
      if (FBTicketRepository().useTicket(ownedTicket.certificateNo!!)) {
        val userName="TEST"
        val url =
          "http://223.194.20.63:8000/entergate/?name=${userName}"
        Logg.d("$url")
        CoroutineScope(Dispatchers.IO).launch{
          // Create URL
          val urlConnection = URL(url)
// Create connection
          val myConnection = urlConnection.openConnection() as HttpURLConnection
          if(myConnection.responseCode==200){
            Logg.d("${myConnection.responseMessage}")
              finishUsing(true)
          }else{
            Logg.e("error : ${myConnection.responseMessage}")
          }
        }
        this@TicketUsingActivity.toast("티켓을 사용 중 입니다.")
      } else {
        this@TicketUsingActivity.toast("티켓 사용에 실패했습니다.")
        BSearching = true
      }
    }
  }
}