package com.example.ft_hangout

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var database: ContactDatabase
    private lateinit var contactList: LinearLayout
    private lateinit var emptyMessage: TextView
    private lateinit var headerTitle: TextView

    private val preferences by lazy {
        getSharedPreferences("ft_hangouts_preferences", MODE_PRIVATE)
    }

    private val appLifecycleObserver = object : DefaultLifecycleObserver {

        override fun onStop(owner: LifecycleOwner) {
            preferences.edit()
                .putLong("background_timestamp", System.currentTimeMillis())
                .apply()
        }

        override fun onStart(owner: LifecycleOwner) {
            val timestamp = preferences.getLong("background_timestamp", 0L)

            if (timestamp != 0L) {
                val dateFormat = SimpleDateFormat(
                    "dd/MM/yyyy HH:mm:ss",
                    Locale.getDefault()
                )

                val formattedTimestamp = dateFormat.format(Date(timestamp))

                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.last_background_time, formattedTimestamp),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private val requestReceiveSmsPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                Toast.makeText(
                    this,
                    getString(R.string.sms_receiving_permission_granted),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.sms_receiving_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestReceiveSmsPermission.launch(Manifest.permission.RECEIVE_SMS)
        }

        database = ContactDatabase(this)

        contactList = findViewById(R.id.contactList)
        emptyMessage = findViewById(R.id.emptyMessage)
        headerTitle = findViewById(R.id.headerTitle)

        applyHeaderColor()

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val selectedColor = when (item.itemId) {
            R.id.colorBlue -> Color.BLUE
            R.id.colorGreen -> Color.GREEN
            R.id.colorRed -> Color.RED
            R.id.colorDefault -> Color.TRANSPARENT
            else -> return super.onOptionsItemSelected(item)
        }

        preferences.edit()
            .putInt("header_color", selectedColor)
            .apply()

        applyHeaderColor()
        return true
    }

    private fun applyHeaderColor() {
        val color = preferences.getInt("header_color", Color.TRANSPARENT)

        headerTitle.setBackgroundColor(color)

        headerTitle.setTextColor(
            if (color == Color.TRANSPARENT || color == Color.GREEN) {
                Color.DKGRAY
            } else {
                Color.WHITE
            }
        )
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
                setTextColor(Color.DKGRAY)
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
                setBackgroundColor(Color.LTGRAY)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
                )
            }
            contactList.addView(divider)
        }
    }
}
