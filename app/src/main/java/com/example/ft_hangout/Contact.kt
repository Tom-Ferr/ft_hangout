package com.example.ft_hangout

data class Contact(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String,
    val address: String
)