package com.chambit.smartgate.ui.login.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.chambit.smartgate.R
import com.chambit.smartgate.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

//    val gifImage =  GlideDrawableImageViewTarget(splashLogo);
    Glide.with(this).load(R.drawable.logo_gif).fitCenter().into(splashLogo)
    launchApp()
  }

  fun launchApp() {
    /**
     *  Firestore 초기화
     */

//    Logg.i("프리퍼런스에 저장된 자동 로그인 유무 : z${UserInfo.autoLoginKey}b")
    Handler().postDelayed({
      // 앞의 과정이 약간의 시간이 필요하거나 한 경우 바로 어떤 명령을 실행하지 않고 잠시 딜레이를 갖고 실행
      /**
       *  // 로그인 고유 값이 있으면 --> 회원가입 진행 끝났다고 생각하고 일단ㄱㄱ -> 수정해야함
       */
      // 앱 설치시에는 isEmpty() 즉, 값이 없다.

    }, 3000)
  }
}
