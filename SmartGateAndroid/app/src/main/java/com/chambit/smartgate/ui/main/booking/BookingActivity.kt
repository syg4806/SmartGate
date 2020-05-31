package com.chambit.smartgate.ui.main.booking

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.chambit.smartgate.R
import com.chambit.smartgate.constant.Constants.PLACE_ID
import com.chambit.smartgate.dataClass.MyTicketData
import com.chambit.smartgate.dataClass.PlaceData
import com.chambit.smartgate.dataClass.TicketData
import com.chambit.smartgate.dataClass.TicketState
import com.chambit.smartgate.extensions.M_D
import com.chambit.smartgate.extensions.format
import com.chambit.smartgate.network.*
import com.chambit.smartgate.ui.main.myticket.MyTicketActivity
import com.chambit.smartgate.util.ChoicePopUp
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_booking.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.MainScope
import java.util.*
import java.util.concurrent.Executors

class BookingActivity : AppCompatActivity(), View.OnClickListener, CoroutineScope by MainScope() {
  var placeInfoData = PlaceData()
  lateinit var id: String
  lateinit var tickets: ArrayList<TicketData>
  val activity = this
  var setMyTicketCount = 0
  lateinit var nextIntent: Intent
  val now = Calendar.getInstance()

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

          launch {
            Toast.makeText(baseContext, "인식 가능한 지문이 등록되어 있지 않습니다.", Toast.LENGTH_LONG).show()
            /*Toast.makeText(applicationContext,
              "인증 오류: $errString", Toast.LENGTH_SHORT)
              .show()*/
          }

        }

        override fun onAuthenticationSucceeded(
          result: BiometricPrompt.AuthenticationResult
        ) {
          super.onAuthenticationSucceeded(result)

          val authenticatedCryptoObject: BiometricPrompt.CryptoObject? =
            result.cryptoObject

          launch {
            Toast.makeText(baseContext, "지문 인증에 성공하였습니다.", Toast.LENGTH_SHORT).show()
            booking()
          }

          // User has verified the signature, cipher, or message
          // authentication code (MAC) associated with the crypto object,
          // so you can use it in your app's crypto-driven workflows.
        }

        override fun onAuthenticationFailed() {
          super.onAuthenticationFailed()
          launch {
            Toast.makeText(baseContext, "지문 인증에 실패하였습니다.", Toast.LENGTH_SHORT).show()
          }
        }
      })

    // Displays the "log in" prompt.
    biometricPrompt.authenticate(promptInfo)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_booking)

    val biometricManager = BiometricManager.from(this)
    when (biometricManager.canAuthenticate()) {
      BiometricManager.BIOMETRIC_SUCCESS ->
        Logg.d("ssmm11 App can authenticate using biometrics.")
      BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
        Logg.e("ssmm11 No biometric features available on this device.")
      BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
        Logg.e("ssmm11 Biometric features are currently unavailable.")
      BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
        Logg.e(
          "ssmm11 The user hasn't associated any biometric credentials " +
            "with their account."
        )
        launch {

        }
      }
    }


    nextIntent = Intent(this, MyTicketActivity::class.java)
    id = intent.getStringExtra(PLACE_ID)!!
    FBPlaceRepository().getPlaceInfo(id) {
      placeInfoData = it
      FBPlaceImageRepository().getPlaceImage(bookingPlaceLogo, placeInfoData.imagePath!!, this)
      FBTicketRepository().getTickets(placeInfoData.name!!, getTicketListener)
      bookingname.text = placeInfoData.name
    }
    paymentButton.setOnClickListener(this)
    ticketDatePicker.setOnClickListener(this)
  }

  // 팝업 띄우는 함수
  lateinit var noticePopup: ChoicePopUp // 전역으로 선언하지 않으면 리스너에서 dismiss 사용 불가.

  override fun onClick(view: View?) {
    when (view!!.id) {
      R.id.paymentButton -> {
        if (bookingCheckBox.isChecked)
          showBiometricPrompt()
        else
          Toast.makeText(this, "결제 동의를 클릭해주세요", Toast.LENGTH_LONG).show()
      }
      R.id.ticketDatePicker -> {
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
        datePicker.show()

      }
    }
  }

  fun booking() {
    setMyTicketCount = (ticketCountSpinner.selectedItem as String).toInt()
    val ticketNo = ticketKindSpinner.selectedItemPosition
    noticePopup = ChoicePopUp(this, "티켓구매",
      "티켓을 구매했습니다. \n\n[${placeInfoData.name},${ticketKindSpinner.selectedItem}, ${ticketCountSpinner.selectedItem} 개]",
      "확인", "선물하기",
      View.OnClickListener {
        FBTicketRepository().buyTicket(
          tickets[ticketNo].placeRef!!.collection(
            "tickets"
          ).document(tickets[ticketNo].id!!), 0L, setMyTicketCount
        )
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
      var arrayAdapter =
        ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, ticketKinds)
      ticketKindSpinner.adapter = arrayAdapter

      arrayAdapter =
        ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, ticketCounts)
      ticketCountSpinner.adapter = arrayAdapter
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
        startActivity(nextIntent)
        finish()
      }
    }
  }
}
