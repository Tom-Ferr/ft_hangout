package com.example.ft_hangout

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ContactDetailsActivity : AppCompatActivity() {

    private lateinit var database: ContactDatabase
    private var contactId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)

        database = ContactDatabase(this)
        contactId = intent.getLongExtra("contact_id", -1L)

        val contact = database.getAllContacts()
            .find { it.id == contactId }

        if (contact == null) {
            Toast.makeText(this, "Contact not found.", Toast.LENGTH_SHORT).show()
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

        findViewById<Button>(R.id.editContactButton).setOnClickListener {
            Toast.makeText(this, "Editing will be added next.", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.deleteContactButton).setOnClickListener {
            Toast.makeText(this, "Deletion will be added next.", Toast.LENGTH_SHORT).show()
        }
    }
}
