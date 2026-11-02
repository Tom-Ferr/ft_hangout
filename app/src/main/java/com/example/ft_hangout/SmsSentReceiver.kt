package com.example.ft_hangout

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class SmsSentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (resultCode) {
            android.app.Activity.RESULT_OK -> {
                val phoneNumber = intent.getStringExtra("phone_number")
                val message = intent.getStringExtra("message")

                if (phoneNumber != null && message != null) {
                    ContactDatabase(context).insertMessage(
                        phoneNumber = phoneNumber,
                        body = message,
                        timestamp = System.currentTimeMillis(),
                        isIncoming = false
                    )
                }

                Toast.makeText(
                    context,
                    context.getString(R.string.sms_sent_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.sms_sending_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
