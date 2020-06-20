package com.chambit.smartgate.dataClass

import com.google.firebase.firestore.DocumentReference

data class OwnedTicket(
  var certificateNo: Long? = null,
  var ticketRef: DocumentReference? = null,
  var used: TicketState? = null,
  var expirationDate: Long? = null
)
