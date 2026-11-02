package com.example.ft_hangout

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConversationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        val conversationLayout = findViewById<LinearLayout>(
            R.id.conversationLayout
        )

        findViewById<Button>(R.id.conversationBackButton)
            .setOnClickListener {
                finish()
            }

        val phoneNumber = intent.getStringExtra("phone_number")

        if (phoneNumber == null) {
            conversationLayout.addView(TextView(this).apply {
                text = getString(R.string.contact_not_found)
                textSize = 18f
            })
            return
        }

        val database = ContactDatabase(this)
        val messages = database.getMessagesForPhone(phoneNumber)

        val dateFormat = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        )

        if (messages.isEmpty()) {
            conversationLayout.addView(TextView(this).apply {
                text = getString(R.string.no_messages)
                textSize = 18f
            })
            return
        }

        messages.forEach { message ->
            val messageView = TextView(this).apply {
                val direction = if (message.isIncoming) {
                    getString(R.string.received)
                } else {
                    getString(R.string.sent)
                }
                val timestamp = dateFormat.format(Date(message.timestamp))

                text = "$direction - $timestamp\n${message.body}"
                textSize = 16f
                setPadding(12, 12, 12, 12)
            }

            conversationLayout.addView(messageView)
        }
    }
}
