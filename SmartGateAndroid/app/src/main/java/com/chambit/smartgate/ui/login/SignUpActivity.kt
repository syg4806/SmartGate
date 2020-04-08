package com.chambit.smartgate.ui.login

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import java.security.MessageDigest


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


    }

    /**
     * 카카오
     */
    /*
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.signingInfo.apkContentsSigners
            } else {
                TODO("VERSION.SDK_INT < P")
            }
            val md = MessageDigest.getInstance("SHA")
            for (signature in signatures) {
                val md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val key = String(Base64.encode(md.digest(), 0))
                Log.d("Hash key:", "$key")
            }
        } catch(e: Exception) {
            Log.e("name not found", e.toString())
        }*/

}
