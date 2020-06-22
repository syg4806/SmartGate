package com.chambit.smartgate.ui.main.booking

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.chambit.smartgate.R
import com.chambit.smartgate.constant.Constants.PLACE_ID
import com.chambit.smartgate.dataClass.PlaceData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.dataClass.TicketGiftState
import com.chambit.smartgate.extensions.M_D
import com.chambit.smartgate.extensions.format
import com.chambit.smartgate.extensions.shortToast
import com.chambit.smartgate.network.FBPlaceImageRepository
import com.chambit.smartgate.network.FBPlaceRepository
import com.chambit.smartgate.network.FBTicketRepository
import com.chambit.smartgate.ui.BaseActivity
import com.chambit.smartgate.ui.main.MainActivity
import com.chambit.smartgate.ui.send.SendTicketActivity
import com.chambit.smartgate.util.ChoicePopUp
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.SharedPref
import kotlinx.android.synthetic.main.activity_booking.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class BookingActivity : BaseActivity(), View.OnClickListener {
  var placeInfoData = PlaceData()
  lateinit var placeId: String
  lateinit var tickets: MutableList<TicketData>
  var setMyTicketCount = 0

  private val executor = Executors.newSingleThreadExecutor()
  private fun showBiometricPrompt() {
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
      .setTitle("Biometric login for my app")
      .setSubtitle("Log in using your biometric credential")
      .setNegativeButtonText("Cancel")
      .build()
    val biometricPrompt = BiometricPrompt(this, executor,
      object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(
          errorCode: Int,
          errString: CharSequence
        ) {
          super.onAuthenticationError(errorCode, errString)
          val intent = Intent(baseContext, PaymentKeyBookingActivity::class.java)
          startActivityForResult(intent, 0)
          launch {
            //  "인식 가능한 지문이 등록되어 있지 않습니다.".show()
          }

        }

        override fun onAuthenticationSucceeded(
          result: BiometricPrompt.AuthenticationResult
        ) {
          super.onAuthenticationSucceeded(result)

          val authenticatedCryptoObject: BiometricPrompt.CryptoObject? =
            result.cryptoObject

          launch {
            // "지문 인증에 성공하였습니다.".show()
            booking()
          }
        }

        override fun onAuthenticationFailed() {
          super.onAuthenticationFailed()
          launch {
            //  "지문 인증에 실패하였습니다.".show()
          }
        }
      })
    // Displays the "log in" prompt.
    biometricPrompt.authenticate(promptInfo)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == 0 && resultCode == 100) {
      booking()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_booking)

    val biometricManager = BiometricManager.from(this)
    when (biometricManager.canAuthenticate()) {
      BiometricManager.BIOMETRIC_SUCCESS -> {
        Logg.d("App can authenticate using biometrics.")
      }
      BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
        Logg.e("No biometric features available on this device.")
      BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
        Logg.e("Biometric features are currently unavailable.")
      BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
        Logg.e(
          "The user hasn't associated any biometric credentials " +
            "with their account."
        )
      }
    }

    placeId = intent.getStringExtra(PLACE_ID)!!
    launch {
      FBPlaceRepository().getPlaceInfo(placeId).let {
        placeInfoData = it
        FBPlaceImageRepository().getPlaceImage(
          bookingPlaceLogo,
          placeInfoData.imagePath!!,
          this@BookingActivity
        )
        tickets(FBTicketRepository().getTickets(placeInfoData.name!!))
        bookingName.text = placeInfoData.name
      }
    }

    paymentButton.setOnClickListener(this)
    ticketDatePicker.setOnClickListener(this)

    currentTime = Calendar.getInstance().timeInMillis
    ticketDatePicker.text = SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(currentTime)
  }

  // 팝업 띄우는 함수
  lateinit var noticePopup: ChoicePopUp // 전역으로 선언하지 않으면 리스너에서 dismiss 사용 불가.
  private var selectedDateFrom: Long? = null
  var currentTime: Long? = null
  override fun onClick(view: View?) {
    when (view!!.id) {
      /**
       * 결제 버튼 클릭시
       */
      R.id.paymentButton -> {
        // 결제 동의 체크박스카 체크 되어있을 때만 결제 진행
        if (bookingCheckBox.isChecked) {
          if (SharedPref.useFingerPrint) { // 지문 인식 기능을 check 한 경우 지문인식으로 결제
            showBiometricPrompt()
          } else { // 지문 인식 기능이 off 인 경우 결제 비밀번호로 결제
            val intent = Intent(baseContext, PaymentKeyBookingActivity::class.java)
            startActivityForResult(intent, 0)
          }
        } else
          this.shortToast("결제 동의를 클릭해주세요")
      }
      R.id.ticketDatePicker -> {
        val now = Calendar.getInstance()
        val datePicker = DatePickerDialog(
          this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            selectedDateFrom = Calendar.getInstance().apply {
              set(Calendar.YEAR, year)
              set(Calendar.MONTH, month)
              set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }.timeInMillis
            ticketDatePicker.text = selectedDateFrom!!.format(M_D)
          },
          now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = System.currentTimeMillis()
        datePicker.show()
      }
    }
  }

  private fun booking() {
    setMyTicketCount = (ticketCountSpinner.selectedItem as String).toInt()
    val ticketNo = ticketKindSpinner.selectedItemPosition
    FBTicketRepository().buyTicket(
      tickets[ticketNo].placeRef!!.collection(
        "tickets"
      ).document(tickets[ticketNo].id!!), 0L, currentTime!!, setMyTicketCount, TicketGiftState.NO_GIFT_YET
    )

    noticePopup = ChoicePopUp(this, R.drawable.ic_popup_title,
      "티켓을 구매했습니다. \n\n[${placeInfoData.name},${ticketKindSpinner.selectedItem}, ${ticketCountSpinner.selectedItem} 개]",
      View.OnClickListener {
        // 메인으로 이동
        startActivity(Intent(this, MainActivity::class.java).apply {
          this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
        finish()
      },
      View.OnClickListener {
        // 선물하기 버튼
        // 친구 목록으로 이동
        // TODO : 친구 목록으로 보낼때 티켓 리스트 갯수( 자기 자신꺼 빼고 ) 보내서 선택하게 하기
        launch {
          startActivity(Intent(this@BookingActivity, SendTicketActivity::class.java).apply {
            this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.putExtra("goToSendActivity", true)
            this.putExtra("sendTicketListFromBooking", FBTicketRepository().getToDayPurchaseTicketList(currentTime!!))
//            FBTicketRepository().getToDayPurchaseTicketList(currentTime!!).let {
//              Logg.d(it.toString())
//            }

          })

        }
        noticePopup.dismiss()
      })
    noticePopup.show()
  }

  private fun tickets(ticketDatas: MutableList<TicketData>) {
    tickets = ticketDatas

    val ticketKinds = ticketDatas.map { it.kinds }
    val ticketCounts = listOf("1", "2", "3", "4", "5")

    ticketKindSpinner.adapter = ArrayAdapter(
      this@BookingActivity,
      R.layout.support_simple_spinner_dropdown_item,
      ticketKinds
    )
    ticketCountSpinner.adapter =
      ArrayAdapter(this@BookingActivity, R.layout.ticket_count_spinner_item, ticketCounts)
  }
}
