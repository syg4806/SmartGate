package com.chambit.smartgate.dataClass

data class CardData(
  var id: String? = null, // 카드 id
  var number: String? = null, // 카드 넘버
  var name: String? = null, // 카드 이름
  var imagePath: String? = null // 카드 이미지 경로
)
