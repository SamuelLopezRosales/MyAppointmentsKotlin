package com.lopez.samuel.myappointment.model

import com.google.gson.annotations.SerializedName



data class Appointment (
        val id: Int,
        val description: String,
        @SerializedName("scheduled_date") val scheduledDate: String,
        val type: String,
        @SerializedName("created_at") val createdAt: String,
        val status: String,
        @SerializedName("scheduled_time_12") val scheduledTime: String,
        val specialty: Specialty,
        val doctor: Doctor

)