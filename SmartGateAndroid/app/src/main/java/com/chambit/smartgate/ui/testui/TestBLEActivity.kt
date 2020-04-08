package com.chambit.smartgate.ui.testui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Region
import android.os.Bundle
import android.os.Handler
import android.os.RemoteException
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chambit.smartgate.R
import kotlinx.android.synthetic.main.activity_test_b_l_e.*
import org.altbeacon.beacon.*


class TestBLEActivity : AppCompatActivity(), BeaconConsumer {
    lateinit var beaconManager: BeaconManager
    // 감지된 비콘들을 임시로 담을 리스트
    var beaconList = ArrayList<Beacon>();
    private val multiplePermissionsCode = 100          //권한
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_b_l_e);
        checkPermissions()
        // 실제로 비콘을 탐지하기 위한 비콘매니저 객체를 초기화
        beaconManager = BeaconManager.getInstanceForApplication(this);

        // 여기가 중요한데, 기기에 따라서 setBeaconLayout 안의 내용을 바꿔줘야 하는듯 싶다.
        // 필자의 경우에는 아래처럼 하니 잘 동작했음.
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        // 비콘 탐지를 시작한다. 실제로는 서비스를 시작하는것.
        beaconManager.bind(this);
    }

    override fun onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    override fun onBeaconServiceConnect() {
       /* beaconManager.removeAllMonitorNotifiers();
        beaconManager.addMonitorNotifier(object : MonitorNotifier {
            override fun didEnterRegion(p0: Region?) {
                Log.i("MonitorNotifier", "I just saw an beacon for the first time!"); }

            override fun didDetermineStateForRegion(state: Int, p1: Region?) {
                Log.i(
                    "MonitorNotifier",
                    "I have just switched from seeing/not seeing beacons: $state"
                ); }

            override fun didExitRegion(p0: Region?) {
                Log.i("MonitorNotifier", "I no longer see an beacon"); }
        })*/
        beaconManager.addRangeNotifier { beacons, region ->
            if (beacons.isNotEmpty()) {
                beaconList.clear()

                beacons.forEach{
                    beaconList.addAll(beacons)
                }
                Log.i(
                    "RangeNotifier",
                    "The first beacon I see is about " + beacons.iterator().next().distance + " meters away."
                )
            }
            Log.d("ah?","ahhahah")
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

    // 버튼이 클릭되면 textView 에 비콘들의 정보를 뿌린다.
    fun onClick(view: View) {
        when (view.id) {
            R.id.testBTN -> {
                // 아래에 있는 handleMessage를 부르는 함수. 맨 처음에는 0초간격이지만 한번 호출되고 나면
                // 1초마다 불러온다.
                Log.d("onClick","???")
                handler.sendEmptyMessage(0);
            }
        }

    }

    var handler = Handler()
    {
        when (it.what) {
            0 -> {
                printBeacon()
            }
        }
        true
    }

    private fun printBeacon() {
        // testTV.text=""
        // 비콘의 아이디와 거리를 측정하여 textView에 넣는다.
        Log.d("printBeacon", "HI")
        beaconList.forEach {
            testTV.append(
                "ID : " + it.id2 + " / " + "Distance : " + String.format(
                    "%.3f",
                    it.distance
                ) + "m\n"
            )
        }
        // 자기 자신을 1초마다 호출
        handler.sendEmptyMessageDelayed(0, 1000)
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

}