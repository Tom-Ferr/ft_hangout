package com.example.ft_hangout

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.widget.Toast

class SmsSentReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val resultMessage = when (resultCode) {
            Activity.RESULT_OK -> "SMS sent successfully"

            SmsManager.RESULT_ERROR_GENERIC_FAILURE ->
                "SMS failed: generic failure"

            SmsManager.RESULT_ERROR_NO_SERVICE ->
                "SMS failed: no cellular service"

            SmsManager.RESULT_ERROR_RADIO_OFF ->
                "SMS failed: radio is off"

            SmsManager.RESULT_ERROR_NULL_PDU ->
                "SMS failed: invalid SMS data"

            else -> "SMS failed (code: $resultCode)"
        }

        Toast.makeText(
            context,
            resultMessage,
            Toast.LENGTH_LONG
        ).show()
    }
}
