package com.lopez.samuel.myappointment.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lopez.samuel.myappointment.R
import com.lopez.samuel.myappointment.model.Appointment
import kotlinx.android.synthetic.main.item_appointment.view.*

class AppointmentAdapter(private val appointments: ArrayList<Appointment>): RecyclerView.Adapter<AppointmentAdapter.ViewHolder>(){

    // representa nuestra vista
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(appointment: Appointment) =
            // estamos accediendo solo al objeto itemView
            with(itemView){
                tvAppointmentId.text = context.getString(R.string.item_appointment_id,appointment.id)
                tvDoctorName.text = appointment.doctorName
                tvScheduledDate.text = context.getString(R.string.item_appointment_date,appointment.scheduledDate)
                tvScheduledTime.text = context.getString(R.string.item_appointment_time,appointment.scheduledTime)
            }

    }

    // inflate XML items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        )
    }

    // Number of Elements
    override fun getItemCount() = appointments.size


    // Bind to fate with data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]

        holder.bind(appointment)

    }
}