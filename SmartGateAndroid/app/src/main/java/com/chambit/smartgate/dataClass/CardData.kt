package com.chambit.smartgate.dataClass

data class CardData(
  var number: String? = null, // 카드 넘버
  var validity: String? = null, // 유효 기간
  var cvc: String? = null, // cvc 번호
  var name: String? = null, // 사용자 이름
  var birth: String? = null // 생년월일
)
