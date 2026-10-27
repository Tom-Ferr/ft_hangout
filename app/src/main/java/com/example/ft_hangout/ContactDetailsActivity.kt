package com.example.ft_hangout

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

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
            val intent = android.content.Intent(
                this,
                AddContactActivity::class.java
            )
            intent.putExtra("contact_id", contactId)
            startActivity(intent)
        }


        findViewById<Button>(R.id.deleteContactButton).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete this contact?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete") { _, _ ->
                    val deletedRows = database.deleteContact(contactId)

                    if (deletedRows > 0) {
                        Toast.makeText(
                            this,
                            "Contact deleted.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Could not delete contact.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .show()
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

}
