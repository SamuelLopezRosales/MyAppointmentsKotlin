package com.lopez.samuel.myappointment.io.response

import com.lopez.samuel.myappointment.model.User

data class LoginResponse(
        val success: Boolean,
        val user: User,
        val access_token: String
)