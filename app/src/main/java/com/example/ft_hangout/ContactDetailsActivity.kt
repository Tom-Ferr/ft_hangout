package com.example.ft_hangout

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ContactDetailsActivity : AppCompatActivity() {

    private lateinit var database: ContactDatabase
    private var contactId: Long = -1L
    private var smsPhoneNumber: String = ""

    private val requestSmsPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                sendSms()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.sms_permission_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)

        database = ContactDatabase(this)
        contactId = intent.getLongExtra("contact_id", -1L)

        val contact = database.getAllContacts()
            .find { it.id == contactId }

        if (contact == null) {
            Toast.makeText(
                this,
                getString(R.string.contact_not_found),
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        findViewById<TextView>(R.id.detailName).text =
            "${contact.firstName} ${contact.lastName}"

        findViewById<TextView>(R.id.detailPhone).text =
            contact.phoneNumber

        findViewById<TextView>(R.id.detailEmail).text =
            contact.email

        findViewById<TextView>(R.id.detailAddress).text =
            contact.address

        findViewById<Button>(R.id.editContactButton)
            .setOnClickListener {
                val editIntent = Intent(
                    this,
                    AddContactActivity::class.java
                )
                editIntent.putExtra("contact_id", contactId)
                startActivity(editIntent)
            }

        findViewById<Button>(R.id.deleteContactButton)
            .setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_contact))
                    .setMessage(getString(R.string.confirm_delete_contact))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(getString(R.string.delete)) { _, _ ->
                        val deletedRows = database.deleteContact(contactId)

                        if (deletedRows > 0) {
                            Toast.makeText(
                                this,
                                getString(R.string.contact_deleted),
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                getString(R.string.could_not_delete_contact),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .show()
            }

        findViewById<Button>(R.id.goBackButton)
            .setOnClickListener {
                finish()
            }

        findViewById<Button>(R.id.sendSmsButton)
            .setOnClickListener {
                smsPhoneNumber = contact.phoneNumber

                val message = findViewById<EditText>(
                    R.id.smsMessageInput
                ).text.toString().trim()

                if (message.isEmpty()) {
                    findViewById<EditText>(
                        R.id.smsMessageInput
                    ).error = getString(R.string.enter_message)
                    return@setOnClickListener
                }

                if (
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.SEND_SMS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    sendSms()
                } else {
                    requestSmsPermission.launch(
                        Manifest.permission.SEND_SMS
                    )
                }
            }

        findViewById<Button>(R.id.viewConversationButton)
            .setOnClickListener {
                val intent = Intent(
                    this,
                    ConversationActivity::class.java
                )
                intent.putExtra("phone_number", contact.phoneNumber)
                startActivity(intent)
            }
    }

    override fun onResume() {
        super.onResume()

        val contact = database.getAllContacts()
            .find { it.id == contactId }

        if (contact == null) {
            finish()
            return
        }

        findViewById<TextView>(R.id.detailName).text =
            "${contact.firstName} ${contact.lastName}"

        findViewById<TextView>(R.id.detailPhone).text =
            contact.phoneNumber

        findViewById<TextView>(R.id.detailEmail).text =
            contact.email

        findViewById<TextView>(R.id.detailAddress).text =
            contact.address
    }

    private fun sendSms() {
        val messageInput = findViewById<EditText>(
            R.id.smsMessageInput
        )
        val message = messageInput.text.toString().trim()

        if (message.isEmpty()) {
            messageInput.error = getString(R.string.enter_message)
            return
        }

        try {
            val smsManager = getSystemService(
                SmsManager::class.java
            )

            val sentIntent = PendingIntent.getBroadcast(
                this,
                0,
                Intent(this, SmsSentReceiver::class.java).apply {
                    putExtra("phone_number", smsPhoneNumber)
                    putExtra("message", message)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

            smsManager.sendTextMessage(
                smsPhoneNumber,
                null,
                message,
                sentIntent,
                null
            )

            messageInput.text.clear()

        } catch (e: Exception) {
            Toast.makeText(
                this,
                getString(R.string.could_not_send_sms, e.message ?: ""),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
