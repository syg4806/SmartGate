package com.chambit.smartgate.ui.login

import com.chambit.smartgate.GlobalApplication
import com.kakao.auth.*

class KakaoSDKAdapter : KakaoAdapter() {
    override fun getSessionConfig(): ISessionConfig {
        return object : ISessionConfig {
            //사용자 인증 수단
            //미지정 시 가능한 모든 옵션 지정

            //KAKAO_TALK	0	카카오톡으로 로그인
            //KAKAO_STORY	1	카카오스토리로 로그인
            //KAKAO_ACCOUNT	2	웹뷰를 통해 카카오계정 로그인
            //KAKAO_LOGIN_ALL	4	모든 로그인 방식 사용
            //KAKAO_TALK_ONLY	5	카카오톡으로 로그인, 실패 시 웹뷰 로그인을 진행하지 않음.
            override fun getAuthTypes(): Array<AuthType> {
                return arrayOf(AuthType.KAKAO_TALK)
            }

            override fun isUsingWebviewTimer(): Boolean {
                return false
            }

            override fun getApprovalType(): ApprovalType? {
                return ApprovalType.INDIVIDUAL
            }

            override fun isSaveFormData(): Boolean {
                return true
            }

            override fun isSecureMode(): Boolean {
                return true
            }
        }
    }

    override fun getApplicationConfig(): IApplicationConfig {
        return IApplicationConfig {
            GlobalApplication.instance?.getGlobalApplicationContext()
        }
    }
}