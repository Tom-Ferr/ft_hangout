package com.example.ft_hangout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var database: ContactDatabase
    private lateinit var contactList: LinearLayout
    private lateinit var emptyMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = ContactDatabase(this)

        contactList = findViewById(R.id.contactList)
        emptyMessage = findViewById(R.id.emptyMessage)

        findViewById<Button>(R.id.addContactButton).setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                24 + systemBars.left,
                24 + systemBars.top,
                24 + systemBars.right,
                24 + systemBars.bottom
            )
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        displayContacts()
    }

    private fun displayContacts() {
        contactList.removeAllViews()

        val contacts = database.getAllContacts()
        emptyMessage.visibility =
            if (contacts.isEmpty()) View.VISIBLE else View.GONE

        for (contact in contacts) {
            val contactView = TextView(this).apply {
                text = "${contact.firstName} ${contact.lastName}\n" +
                        "${contact.phoneNumber}\n${contact.email}"
                textSize = 16f
                setTextColor(android.graphics.Color.DKGRAY)
                setPadding(0, 20, 0, 20)

                isClickable = true
                isFocusable = true

                setOnClickListener {
                    val intent = Intent(
                        this@MainActivity,
                        ContactDetailsActivity::class.java
                    )
                    intent.putExtra("contact_id", contact.id)
                    startActivity(intent)
                }
            }

            contactList.addView(contactView)

            val divider = View(this).apply {
                setBackgroundColor(android.graphics.Color.LTGRAY)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
                )
            }
            contactList.addView(divider)
        }
    }
}
