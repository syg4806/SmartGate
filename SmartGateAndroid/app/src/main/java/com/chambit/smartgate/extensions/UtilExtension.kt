package com.chambit.smartgate.extensions

import android.content.Context
import android.widget.Toast
import java.nio.ByteBuffer
import java.util.*

fun Context.shortToast(str: String) {
  Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
}
fun Context.longToast(str: String) {
  Toast.makeText(this, str, Toast.LENGTH_LONG).show()
}
fun UUID.toByteArray(): ByteArray {
  val bb: ByteBuffer = ByteBuffer.wrap(ByteArray(16))
  bb.putLong(this.mostSignificantBits)
  bb.putLong(this.leastSignificantBits)
  return bb.array()
}