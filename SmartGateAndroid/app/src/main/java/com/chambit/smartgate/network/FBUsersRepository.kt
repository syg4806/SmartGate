package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.CardData
import com.chambit.smartgate.dataClass.UserInfo
import com.chambit.smartgate.util.SharedPref
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.tasks.await

class FBUsersRepository : BaseFB() {
  fun userSignUp(email: String?) {
    var email1=""
    if(email.isNullOrEmpty()) email1="null"
    else email1=email
    val userInfo = UserInfo(SharedPref.autoLoginKey, email1, SharedPref.paymentKey)
    userRef.document(userInfo.uid!!).set(userInfo)
  }

  suspend fun setUserCard(cardData: CardData): DocumentReference? {
    return userRef.document(SharedPref.autoLoginKey).collection(CARD).add(cardData).await()
  }

  suspend fun getUserCard(): CardData? {
    return userRef.document(SharedPref.autoLoginKey).collection(CARD).get()
      .await().documents.let {
        if (it.isEmpty()) null
        else it.first().toObject(CardData::class.java)
      }
  }

  suspend fun deleteUserCard() {
    userRef.document(SharedPref.autoLoginKey).collection(CARD)
      .get()
      .await()
      .documents.first().reference.delete()
  }
}