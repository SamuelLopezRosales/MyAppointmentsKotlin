package com.lopez.samuel.myappointment.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.lopez.samuel.myappointment.R
import com.lopez.samuel.myappointment.model.Appointment
import kotlinx.android.synthetic.main.activity_appointments.*

class AppointmentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)


        rvAppointments.layoutManager = LinearLayoutManager(this) // va ser lineal // gridLinealManager

        val appointments = ArrayList<Appointment>()
        appointments.add(Appointment(1,"Medico Test","12/12/2021","4:00 PM"))
        appointments.add(Appointment(2,"Medico Test","12/12/2021","4:00 PM"))
        appointments.add(Appointment(3,"Medico Test","12/12/2021","4:00 PM"))

        rvAppointments.adapter = AppointmentAdapter(appointments)
    }
}
