
package com.example.ft_hangout

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddContactActivity : AppCompatActivity() {

    private lateinit var database: ContactDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        database = ContactDatabase(this)

        val firstNameInput = findViewById<EditText>(R.id.firstNameInput)
        val lastNameInput = findViewById<EditText>(R.id.lastNameInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val addressInput = findViewById<EditText>(R.id.addressInput)
        val saveButton = findViewById<Button>(R.id.saveContactButton)

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
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phone,
                email = email,
                address = address
            )

            val id = database.insertContact(contact)

            if (id != -1L) {
                Toast.makeText(
                    this,
                    "Contact saved successfully!",
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
