package com.chambit.smartgate.ui.main.booking

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.chambit.smartgate.BaseActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.constant.Constants.PLACE_ID
import com.chambit.smartgate.dataClass.MyTicketData
import com.chambit.smartgate.dataClass.PlaceData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.dataClass.TicketState
import com.chambit.smartgate.extension.show
import com.chambit.smartgate.extensions.M_D
import com.chambit.smartgate.extensions.format
import com.chambit.smartgate.network.*
import com.chambit.smartgate.ui.main.myticket.MyTicketActivity
import com.chambit.smartgate.util.ChoicePopUp
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.SharedPref
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_booking.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class BookingActivity : BaseActivity(), View.OnClickListener {
  var placeInfoData = PlaceData()
  lateinit var placeId: String
  lateinit var tickets: ArrayList<TicketData>
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
          // User has verified the signature, cipher, or message
          // authentication code (MAC) associated with the crypto object,
          // so you can use it in your app's crypto-driven workflows.
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
        launch {

        }
      }
    }

    placeId = intent.getStringExtra(PLACE_ID)!!
    FBPlaceRepository().getPlaceInfo(placeId) {
      placeInfoData = it
      FBPlaceImageRepository().getPlaceImage(bookingPlaceLogo, placeInfoData.imagePath!!, this)
      FBTicketRepository().getTickets(placeInfoData.name!!, getTicketListener)
      bookingName.text = placeInfoData.name
    }
    paymentButton.setOnClickListener(this)
    ticketDatePicker.setOnClickListener(this)

    val currentTime = Calendar.getInstance().time
    ticketDatePicker.text = SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(currentTime)
  }

  // 팝업 띄우는 함수
  lateinit var noticePopup: ChoicePopUp // 전역으로 선언하지 않으면 리스너에서 dismiss 사용 불가.

  override fun onClick(view: View?) {
    when (view!!.id) {
      /**
       * 결제 버튼 클릭시
       */
      // TODO 회원가입 시 , 묻기 |||| 지문인식 결제 할 때 검사해서 하기
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
          "결제 동의를 클릭해주세요".show()
      }
      R.id.ticketDatePicker -> {
        val now = Calendar.getInstance()
        val datePicker = DatePickerDialog(
          this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val selectedDateFrom = Calendar.getInstance().apply {
              set(Calendar.YEAR, year)
              set(Calendar.MONTH, month)
              set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }.timeInMillis
            ticketDatePicker.text = selectedDateFrom.format(M_D)
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
      ).document(tickets[ticketNo].id!!), 0L, setMyTicketCount
    )
    noticePopup = ChoicePopUp(this,
      "티켓을 구매했습니다. \n\n[${placeInfoData.name},${ticketKindSpinner.selectedItem}, ${ticketCountSpinner.selectedItem} 개]",
      View.OnClickListener {
        finish()
      },
      View.OnClickListener {
        noticePopup.dismiss()
      })
    noticePopup.show()
  }

  private val getTicketListener = object : GetTicketListener {
    override fun tickets(ticketDatas: ArrayList<TicketData>) {
      tickets = ticketDatas

      val ticketKinds = arrayListOf<String>()
      val ticketCounts = arrayListOf<String>()
      ticketDatas.forEach {
        ticketKinds.add(it.kinds!!)
      }
      for (i in 1..5) {
        ticketCounts.add(i.toString())
      }

      ticketKindSpinner.adapter = ArrayAdapter(
        this@BookingActivity,
        R.layout.support_simple_spinner_dropdown_item,
        ticketKinds
      )
      ticketCountSpinner.adapter =
        ArrayAdapter(this@BookingActivity, R.layout.ticket_count_spinner_item, ticketCounts)
    }

    override fun myTickets(
      myTicketDatas: ArrayList<MyTicketData>,
      ticketDatas: ArrayList<TicketData>
    ) {
    }

    override fun getTicketReference(reference: DocumentReference) {
      val dt = Date()
      val myticket = MyTicketData(dt.time.toString(), reference, 0L, TicketState.UNUSED)
      FBTicketRepository().setMyTicket(myticket, setTicketListener)
    }
  }

  val setTicketListener = object : SetTicketListener {
    override fun setMyTicket() {
      setMyTicketCount--
      if (setMyTicketCount == 0) {
        startActivity(Intent(this@BookingActivity, MyTicketActivity::class.java))
        finish()
      }
    }
  }
}
