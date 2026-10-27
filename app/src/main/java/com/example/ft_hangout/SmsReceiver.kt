package com.example.ft_hangout

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.PhoneNumberUtils
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            return
        }

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isEmpty()) return

        val sender = messages.first().originatingAddress ?: return
        val body = messages.joinToString("") { it.messageBody.orEmpty() }
        val timestamp = messages.first().timestampMillis

        val database = ContactDatabase(context)

        database.insertMessage(
            phoneNumber = sender,
            body = body,
            timestamp = timestamp,
            isIncoming = true
        )

        val contact = database.getAllContacts().find {
            PhoneNumberUtils.compare(it.phoneNumber, sender)
        }

        val senderName = if (contact != null) {
            "${contact.firstName} ${contact.lastName}"
        } else {
            sender
        }

        Toast.makeText(
            context,
            "SMS received from $senderName: $body",
            Toast.LENGTH_LONG
        ).show()
    }
}
