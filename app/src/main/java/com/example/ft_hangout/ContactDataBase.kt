package com.example.ft_hangout

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ContactDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_CONTACTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FIRST_NAME TEXT NOT NULL,
                $COLUMN_LAST_NAME TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_ADDRESS TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Database migrations will be added here when needed.
    }

    fun insertContact(contact: Contact): Long {
        val values = ContentValues().apply {
            put(COLUMN_FIRST_NAME, contact.firstName)
            put(COLUMN_LAST_NAME, contact.lastName)
            put(COLUMN_PHONE, contact.phoneNumber)
            put(COLUMN_EMAIL, contact.email)
            put(COLUMN_ADDRESS, contact.address)
        }

        return writableDatabase.insert(TABLE_CONTACTS, null, values)
    }

    fun getAllContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()

        readableDatabase.query(
            TABLE_CONTACTS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_LAST_NAME ASC, $COLUMN_FIRST_NAME ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                contacts.add(
                    Contact(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)),
                        lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)),
                        phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                        email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS))
                    )
                )
            }
        }

        return contacts
    }

    fun updateContact(contact: Contact): Int {
        val values = ContentValues().apply {
            put(COLUMN_FIRST_NAME, contact.firstName)
            put(COLUMN_LAST_NAME, contact.lastName)
            put(COLUMN_PHONE, contact.phoneNumber)
            put(COLUMN_EMAIL, contact.email)
            put(COLUMN_ADDRESS, contact.address)
        }

        return writableDatabase.update(
            TABLE_CONTACTS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(contact.id.toString())
        )
    }

    fun deleteContact(id: Long): Int {
        return writableDatabase.delete(
            TABLE_CONTACTS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    companion object {
        private const val DATABASE_NAME = "contacts.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_CONTACTS = "contacts"
        private const val COLUMN_ID = "id"
        private const val COLUMN_FIRST_NAME = "first_name"
        private const val COLUMN_LAST_NAME = "last_name"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_ADDRESS = "address"
    }
}
