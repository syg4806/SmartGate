package com.chambit.smartgate.dataClass

import com.google.firebase.firestore.DocumentReference

data class TicketData(
  var id: String? = null, // 티켓 id
  var placeRef: DocumentReference? = null, // 티켓 장소
  var kinds: String? = null, // 티켓 종  류
  var imagePath: String? = null // 티켓 이미지 경로
)
