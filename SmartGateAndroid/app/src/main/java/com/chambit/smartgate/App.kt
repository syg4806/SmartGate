package com.chambit.smartgate

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.chambit.smartgate.ui.login.KakaoSDKAdapter
import com.kakao.auth.KakaoSDK

class App : Application() {
  companion object {
    var activityList = ArrayList<Activity>()
    lateinit var instance: App
      private set
  }

  private var activityCount = 0
  private val activityLifecycleCallbacks = ActivityLifecycleCallbacks()

  override fun onCreate() {
    super.onCreate()
    instance = this

    KakaoSDK.init(KakaoSDKAdapter())
    registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
  }

  fun context(): Context = applicationContext

  fun currentActivity(): Activity? = activityLifecycleCallbacks.currentActivity

  inner class ActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    var currentActivity: Activity? = null

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
      currentActivity = activity
      activityList.add(currentActivity!!)
      activityCount++
    }

    override fun onActivityStarted(activity: Activity?) {}
    override fun onActivityResumed(activity: Activity?) {
      currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity?) {}
    override fun onActivityStopped(activity: Activity?) {}
    override fun onActivityDestroyed(activity: Activity?) {
      activityCount--
      activityList.remove(activity)
      // 앱을 완전히 종료할 때 구독 끊기
      if (activityCount == 0) {
        destroyAllRepository()
      }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}
  }

  fun destroyAllRepository() {
    /* AreaRepository.instance.destroy()
     NoticeRepository.instance.destroy()
     UserRepository.instance.destroy()
     QnaRepository.instance.destroy()
     DrivingRepository.instance.destroy()
     MapLocationRepository.instance.destroy()
     AnalyticsManager.destroy()*/
  }

}