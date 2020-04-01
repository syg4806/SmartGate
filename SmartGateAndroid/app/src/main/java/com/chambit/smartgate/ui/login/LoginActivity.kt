package com.chambit.smartgate.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.ui.main.MainActivity
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException


class LoginActivity : AppCompatActivity() {
    private var callback: SessionCallback = SessionCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 세션 콜백 등록
        Session.getCurrentSession().addCallback(callback)
    }


    override fun onDestroy() {
        Session.getCurrentSession().removeCallback(callback);
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Log.d("ssmm11", "session get current session")
            return

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private class SessionCallback : ISessionCallback {
        override fun onSessionOpenFailed(exception: KakaoException?) {
            Log.d("ssmm11","Session Call back :: onSessionOpenFailed: ${exception?.message}")
        }

        override fun onSessionOpened() {
            UserManagement.getInstance().me(object : MeV2ResponseCallback() {


                override fun onFailure(errorResult: ErrorResult?) {
                    Log.d("ssmm11", "Session Call back :: on failed ${errorResult?.errorMessage}")
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    Log.d("ssmm11","Session Call back :: onSessionClosed ${errorResult?.errorMessage}")
                }

                override fun onSuccess(result: MeV2Response?) {
                    // register or login
                    //TODO: DB에 카카오톡 정보 업르드

                    Log.d("ssmm11", result!!.id.toString())
                    Log.d("ssmm11", result.kakaoAccount.email)
                    //TODO: 메인 액티비티로 넘어가서 할 일
                    //val intent = Intent(baseContext, MainActivity::class.java)

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