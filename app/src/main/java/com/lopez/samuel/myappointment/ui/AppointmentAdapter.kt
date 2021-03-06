package com.lopez.samuel.myappointment.ui

import android.support.v7.widget.RecyclerView
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lopez.samuel.myappointment.R
import com.lopez.samuel.myappointment.model.Appointment
import kotlinx.android.synthetic.main.item_appointment.view.*

class AppointmentAdapter
    : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>(){

    var appointments = ArrayList<Appointment>()

    // representa nuestra vista
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(appointment: Appointment) =
            // estamos accediendo solo al objeto itemView
            with(itemView){
                tvAppointmentId.text = context.getString(R.string.item_appointment_id,appointment.id)
                tvDoctorName.text = appointment.doctor.name
                tvScheduledDate.text = context.getString(R.string.item_appointment_date,appointment.scheduledDate)
                tvScheduledTime.text = context.getString(R.string.item_appointment_time,appointment.scheduledTime)

                tvSpecialty.text = appointment.specialty.name
                tvDescription.text = appointment.description
                tvStatus.text = appointment.status
                tvType.text = appointment.type
                tvCreatedAt.text = context.getString(R.string.label_created_at,appointment.createdAt)

                ibExpand.setOnClickListener{
                    TransitionManager.beginDelayedTransition(parent as ViewGroup, AutoTransition())

                    if(LinearLayoutDetails.visibility == View.VISIBLE) {
                        LinearLayoutDetails.visibility = View.GONE
                        ibExpand.setImageResource(R.drawable.ic_expand_more)
                    }else{
                        LinearLayoutDetails.visibility = View.VISIBLE
                        ibExpand.setImageResource(R.drawable.ic_expand_less)
                    }

                }
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