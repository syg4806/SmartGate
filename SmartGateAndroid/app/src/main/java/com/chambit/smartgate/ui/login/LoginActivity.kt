package com.chambit.smartgate.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.SharedPref
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException

class LoginActivity : AppCompatActivity() {
  private var callback: SessionCallback = SessionCallback()

  //@RequiresApi(Build.VERSION_CODES.P)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    //hashKey()
    // 세션 콜백 등록
    Session.getCurrentSession().addCallback(callback)
  }

  /**
   * 해당 함수는 kakao Developer 등록을 위해 남겨둔 코드입니다. 주석 처리 해놓았습니다.
   */

  /*@RequiresApi(Build.VERSION_CODES.P)
  private fun hashKey() = try {
    val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
    val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      info.signingInfo.apkContentsSigners
    } else {
    }
    val md = MessageDigest.getInstance("SHA")
    for (signature in signatures) {
      md.update(signature.toByteArray())
      val key = String(Base64.encode(md.digest(), 0))
      Logg.d("Hash key: $key")
    }
  } catch (e: Exception) {
    Logg.e("name not found $e")
  }*/


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
          Logg.d(result!!.id.toString())
          SharedPref.autoLoginKey = result.id.toString()
          Logg.d(result.kakaoAccount.email)


          val intent = Intent(baseContext, PaymentKeySettingActivity::class.java)
          intent.putExtra("email", result.kakaoAccount.email)
          startActivity(intent)
          finish()
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
            Logg.i("KAKAO_SESSION", "로그인 성공")
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            Logg.d("","KAKAO_SESSION", "로그인 실패", exception)
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