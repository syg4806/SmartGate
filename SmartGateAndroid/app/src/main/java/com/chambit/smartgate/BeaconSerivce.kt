package com.chambit.smartgate

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.chambit.smartgate.util.Logg
import com.google.firebase.firestore.FirebaseFirestore
import org.altbeacon.beacon.*

class BeaconSerivce : Service(), BeaconConsumer {
    lateinit var beaconManager: BeaconManager

    // 감지된 비콘들을 임시로 담을 리스트
    var beaconList = ArrayList<Beacon>();

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // 실제로 비콘을 탐지하기 위한 비콘매니저 객체를 초기화
        beaconManager = BeaconManager.getInstanceForApplication(this);

        // 여기가 중요한데, 기기에 따라서 setBeaconLayout 안의 내용을 바꿔줘야 하는듯 싶다.
        // 필자의 경우에는 아래처럼 하니 잘 동작했음.
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT))
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT))
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT))
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT))
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.URI_BEACON_LAYOUT))

        // 비콘 탐지를 시작한다. 실제로는 서비스를 시작하는것.
        beaconManager.bind(this);
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //startForeground(1, Notification());

        return START_STICKY
    }

    override fun onDestroy() {
        beaconManager.unbind(this);
    }

    override fun onBeaconServiceConnect() {
        beaconManager.addRangeNotifier { beacons, region ->
            if (beacons.isNotEmpty()) {
                beaconList.clear()

                beaconList.addAll(beacons)
                Log.i(
                    "RangeNotifier",
                    "The first beacon I see is about " + beacons.iterator()
                        .next().distance + " meters away."
                )
                printBeacon()
            }
            Log.d("ah?", "ahhahah")
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

    private fun printBeacon() {
        // testTV.text=""
        // 비콘의 아이디와 거리를 측정하여 textView에 넣는다.
        Log.d("printBeacon", "HI")
        beaconList.forEach {
            Logg.d("ID : " + it.id2 + " / " + "Distance : " + String.format("%.3f", it.distance))

            val db = FirebaseFirestore.getInstance()

            db.collection("place").whereArrayContains("UID", it.id2.toString())
                .get()
                .addOnSuccessListener { result2 ->

                    Logg.d("ssmm11 onscuc = ${result2.documents.size}")
                    result2.documents.forEach {
                        it.reference.collection("tickets")
                            .get()
                            .addOnSuccessListener {result ->
                                for (document in result) {
                                    val ticketId = document.getString("ticketId")
                                    Logg.d("ssmm11 ticketId = $ticketId")

                                }

                            }
                    }
                }
        }
    }
}
