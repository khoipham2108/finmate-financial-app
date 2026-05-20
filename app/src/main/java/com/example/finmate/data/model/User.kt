package com.example.finmate.data.model

import com.google.firebase.Timestamp

data class User(
    val email: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
