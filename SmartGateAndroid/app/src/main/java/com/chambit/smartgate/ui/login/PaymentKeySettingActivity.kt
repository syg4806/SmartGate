package com.chambit.smartgate.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.chambit.smartgate.R
import com.chambit.smartgate.network.FBUsersRepository
import com.chambit.smartgate.ui.main.MainActivity
import com.chambit.smartgate.util.Logg
import com.chambit.smartgate.util.SharedPref
import kotlinx.android.synthetic.main.activity_booking.*
import kotlinx.android.synthetic.main.activity_booking.paymentButton
import kotlinx.android.synthetic.main.activity_payment_key_setting.*

class PaymentKeySettingActivity : AppCompatActivity(), View.OnClickListener {
  var password = ""
  var passwordConfirmation = false
  private val numberList = arrayListOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_payment_key_setting)

    numberList.shuffle()
    setNumber(numberList)

    password_1.setOnClickListener(this)
    password_2.setOnClickListener(this)
    password_3.setOnClickListener(this)
    password_4.setOnClickListener(this)
    password_5.setOnClickListener(this)
    password_6.setOnClickListener(this)
    password_7.setOnClickListener(this)
    password_8.setOnClickListener(this)
    password_9.setOnClickListener(this)
    password_10.setOnClickListener(this)
    password_11.setOnClickListener(this)
    password_12.setOnClickListener(this)
  }

  private fun setNumber(list: ArrayList<String>) {
    password_1.text = list[0]
    password_2.text = list[1]
    password_3.text = list[2]
    password_4.text = list[3]
    password_5.text = list[4]
    password_6.text = list[5]
    password_7.text = list[6]
    password_8.text = list[7]
    password_9.text = list[8]
    password_11.text = list[9]
  }

  private fun imageInit() {
    dot1.setImageResource(R.drawable.dot)
    dot2.setImageResource(R.drawable.dot)
    dot3.setImageResource(R.drawable.dot)
    dot4.setImageResource(R.drawable.dot)
    dot5.setImageResource(R.drawable.dot)
  }

  private fun check(mode: Boolean) {
    if (password.length == 1) {
      if (mode) {
        dot1.setImageResource(R.drawable.dot_fill)
      }
      else {
        dot1.setImageResource(R.drawable.dot)
        password = password.subSequence(0, password.length-1).toString()
      }
    }
    if (password.length == 2) {
      if (mode) {
        dot2.setImageResource(R.drawable.dot_fill)
      }
      else {
        dot2.setImageResource(R.drawable.dot)
        password = password.subSequence(0, password.length-1).toString()
      }
    }
    if (password.length == 3) {
      if (mode) {
        dot3.setImageResource(R.drawable.dot_fill)
      }
      else {
        dot3.setImageResource(R.drawable.dot)
        password = password.subSequence(0, password.length-1).toString()
      }
    }

    if (password.length == 4) {
      if (mode) {
        dot4.setImageResource(R.drawable.dot_fill)
      }
      else {
        dot4.setImageResource(R.drawable.dot)
        password = password.subSequence(0, password.length-1).toString()
      }
    }

    if (password.length == 5) {
      if (mode) {
        dot5.setImageResource(R.drawable.dot_fill)
      }
      else {
        dot5.setImageResource(R.drawable.dot)
        password = password.subSequence(0, password.length-1).toString()
      }
    }
    if (password.length == 6) {
      val email = intent.getStringExtra("email")
      // TODO 비밀번호 확인 만들기
      if (!passwordConfirmation) {
        SharedPref.paymentKey = password
        password = ""
        imageInit()
        numberList.shuffle()
        setNumber(numberList)
        Toast.makeText(this, "비밀 번호 확인 입력을 해주세요", Toast.LENGTH_LONG).show()
        passwordConfirmation = true
      }
      else {
        if (SharedPref.paymentKey == password) {
          FBUsersRepository().userSignUp(email!!)
          val intent = Intent(baseContext, MainActivity::class.java)
          startActivity(intent)
          finish()
        }
        else {
          Toast.makeText(this, "비밀 번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
          password = ""
          imageInit()
          numberList.shuffle()
          setNumber(numberList)
        }
      }
    }
  }

  override fun onClick(v: View?) {
    when (v!!.id) {
      R.id.password_1 -> {
        password += numberList[0]
        check(true)
      }
      R.id.password_2 -> {
        password += numberList[1]
        check(true)
      }
      R.id.password_3 -> {
        password += numberList[2]
        check(true)
      }
      R.id.password_4 -> {
        password += numberList[3]
        check(true)
      }
      R.id.password_5 -> {
        password += numberList[4]
        check(true)
      }
      R.id.password_6 -> {
        password += numberList[5]
        check(true)
      }
      R.id.password_7 -> {
        password += numberList[6]
        check(true)
      }
      R.id.password_8 -> {
        password += numberList[7]
        check(true)
      }
      R.id.password_9 -> {
        password += numberList[8]
        check(true)
      }
      R.id.password_10 -> {
        numberList.shuffle()
        setNumber(numberList)
      }
      R.id.password_11 -> {
        password += numberList[9]
        check(true)
      }
      R.id.password_12 -> {
        if (password.isNotEmpty()) {
          check(false)
        }
      }
    }
  }
}