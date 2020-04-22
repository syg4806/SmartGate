package com.chambit.smartgate.ui.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.ui.main.MainActivity
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.SharedPref
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import java.security.MessageDigest


class LoginActivity : AppCompatActivity() {
  private var callback: SessionCallback = SessionCallback()
  lateinit var mContext: Context

  @RequiresApi(Build.VERSION_CODES.P)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    hashKey()
    mContext = baseContext
    // 세션 콜백 등록
    Session.getCurrentSession().addCallback(callback)
  }

  @RequiresApi(Build.VERSION_CODES.P)
  fun hashKey() {
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
        Logg.d("Hash key: $key")
      }
    } catch (e: Exception) {
      Logg.e("name not found ${e.toString()}")
    }
  }


  override fun onDestroy() {
    // 세션 콜백 삭제
    Session.getCurrentSession().removeCallback(callback);
    super.onDestroy()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
    if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
      Logg.d("session get current session")
      return
    }


    super.onActivityResult(requestCode, resultCode, data)
  }

  inner class SessionCallback : ISessionCallback {
    override fun onSessionOpenFailed(exception: KakaoException?) {
      Logg.d("Session Call back :: onSessionOpenFailed: ${exception?.message}")
    }

    override fun onSessionOpened() { // 로그인 성공
      UserManagement.getInstance().me(object : MeV2ResponseCallback() {


        override fun onFailure(errorResult: ErrorResult?) {
          Logg.d("Session Call back :: on failed ${errorResult?.errorMessage}")
        }

        override fun onSessionClosed(errorResult: ErrorResult?) {
          Logg.d("Session Call back :: onSessionClosed ${errorResult?.errorMessage}")
        }

        override fun onSuccess(result: MeV2Response?) {
          // register or login
          //TODO: DB에 카카오톡 정보 업르드

          Logg.d(result!!.id.toString())
          SharedPref.autoLoginKey = result.id.toString()
          Logg.d(result.kakaoAccount.email)


          //TODO: 메인 액티비티로 넘어가서 할 일
          val intent = Intent(baseContext, MainActivity::class.java)
          startActivity(intent)

          // 로그아웃 코드
//      UserManagement.getInstance()
//        .requestLogout(object : LogoutResponseCallback() {
//          override fun onCompleteLogout() {
//            Logg.e("로그아웃 완료")
//          }
//        })
        }
      })
    }
  }
}


/* // 세션 콜백 구현
    private val sessionCallback: ISessionCallback = object : ISessionCallback {
        override fun onSessionOpened() {
            Log.i("KAKAO_SESSION", "로그인 성공")
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            Log.d("ssmmm11","KAKAO_SESSION", "로그인 실패", exception)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 세션 콜백 등록
        Session.getCurrentSession().addCallback(sessionCallback)
    }

    override fun onDestroy() {
        super.onDestroy()

        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {

        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }*/