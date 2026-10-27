package com.example.ft_hangout

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddContactActivity : AppCompatActivity() {

    private lateinit var database: ContactDatabase
    private var editingContactId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        database = ContactDatabase(this)
        editingContactId = intent.getLongExtra("contact_id", -1L)

        val firstNameInput = findViewById<EditText>(R.id.firstNameInput)
        val lastNameInput = findViewById<EditText>(R.id.lastNameInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val addressInput = findViewById<EditText>(R.id.addressInput)
        val saveButton = findViewById<Button>(R.id.saveContactButton)

        findViewById<Button>(R.id.cancelContactButton).setOnClickListener {
            finish()
        }


        if (editingContactId != -1L) {
            val contact = database.getAllContacts()
                .find { it.id == editingContactId }

            if (contact == null) {
                Toast.makeText(this, "Contact not found.", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            firstNameInput.setText(contact.firstName)
            lastNameInput.setText(contact.lastName)
            phoneInput.setText(contact.phoneNumber)
            emailInput.setText(contact.email)
            addressInput.setText(contact.address)

            saveButton.text = "Update Contact"
        }

        saveButton.setOnClickListener {
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val address = addressInput.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() ||
                phone.isEmpty() || email.isEmpty() || address.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please fill in all fields.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val contact = Contact(
                id = if (editingContactId == -1L) 0L else editingContactId,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phone,
                email = email,
                address = address
            )

            val success = if (editingContactId == -1L) {
                database.insertContact(contact) != -1L
            } else {
                database.updateContact(contact) > 0
            }

            if (success) {
                Toast.makeText(
                    this,
                    if (editingContactId == -1L) {
                        "Contact saved successfully!"
                    } else {
                        "Contact updated successfully!"
                    },
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Could not save contact.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
