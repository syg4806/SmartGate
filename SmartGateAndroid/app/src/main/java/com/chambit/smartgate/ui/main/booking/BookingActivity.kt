package com.chambit.smartgate.ui.main.booking

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chambit.smartgate.R
import com.chambit.smartgate.ui.main.myticket.MyTicketActivity
import com.chambit.smartgate.util.ChoicePopUp
import kotlinx.android.synthetic.main.activity_booking.*

class BookingActivity : AppCompatActivity(), View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        paymentButton.setOnClickListener(this)
    }
    // 팝업 띄우는 함수
    lateinit var noticePopup: ChoicePopUp // 전역으로 선언하지 않으면 리스너에서 dismiss 사용 불가.
    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.paymentButton->{
                noticePopup = ChoicePopUp(this, "티켓구매",
                    "티켓을 구매했습니다. \n\n[장소,종류,날짜,장 수]",
                    "확인", "선물하기",
                    View.OnClickListener {
                        val nextIntent = Intent(this, MyTicketActivity::class.java)
                        startActivity(nextIntent)
                        finish()
                    },
                    View.OnClickListener {
                        noticePopup.dismiss()
                    })
                noticePopup.show()
            }
        }
    }
}
