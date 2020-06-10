package com.chambit.smartgate.network

import com.chambit.smartgate.dataClass.CardData
import com.chambit.smartgate.dataClass.UserInfo
import com.chambit.smartgate.util.SharedPref
import kotlinx.coroutines.tasks.await

class FBUsersRepository : BaseFB() {
  fun userSignUp(email: String) {
    val userInfo = UserInfo(SharedPref.autoLoginKey, email, SharedPref.paymentKey)
    db.collection("users").document(userInfo.uid!!).set(userInfo)
  }

  fun setUserCard(cardData: CardData) {
    db.collection("users").document(SharedPref.autoLoginKey).collection("card").add(cardData)
  }

  suspend fun getUserCard(): CardData? {
    val documentSnapshots =
      db.collection("users").document(SharedPref.autoLoginKey).collection("card").get()
        .await()
        .documents
    return if (documentSnapshots.isEmpty()) {
      null
    } else {
      documentSnapshots.first().toObject(CardData::class.java)
    }
  }

  suspend fun deleteUserCard() {
    db.collection("users").document(SharedPref.autoLoginKey).collection("card")
      .get()
      .await()
      .documents.first().reference.delete()
  }
}