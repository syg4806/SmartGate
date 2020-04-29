package com.chambit.smartgate.dataClass

import com.google.firebase.firestore.DocumentReference

data class MyTicketData(
  var certificateNo: String? = null, // 티켓 id
  var ticketRef: DocumentReference? = null, // db 에 저장되어있는 실제 티켓 경로
  var expirationDate: Long? = null, // 티켓 개수
  var isUsed: Enum<TicketState>? = null // 티켓 상태
)

