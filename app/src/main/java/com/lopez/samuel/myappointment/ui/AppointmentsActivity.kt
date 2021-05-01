package com.lopez.samuel.myappointment.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.lopez.samuel.myappointment.R
import com.lopez.samuel.myappointment.io.ApiService
import com.lopez.samuel.myappointment.model.Appointment
import com.lopez.samuel.myappointment.util.PreferenceHelper
import com.lopez.samuel.myappointment.util.PreferenceHelper.get
import com.lopez.samuel.myappointment.util.toast
import kotlinx.android.synthetic.main.activity_appointments.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentsActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy{
        ApiService.create()
    }

    private val preferences by lazy{
        PreferenceHelper.defaultPrefs(this)
    }

    private val appointmentAdapter = AppointmentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointments)

        loadAppointments()


        rvAppointments.layoutManager = LinearLayoutManager(this) // va ser lineal // gridLinealManager
        rvAppointments.adapter = appointmentAdapter
    }


    private fun loadAppointments(){
        val access_token = preferences["access_token",""]
        val call = apiService.getAppointments("Bearer $access_token")
        call.enqueue(object: Callback<ArrayList<Appointment>>{
            override fun onFailure(call: Call<ArrayList<Appointment>>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<ArrayList<Appointment>>, response: Response<ArrayList<Appointment>>) {
               if(response.isSuccessful){
                   // arreglo de appointments
                   response.body()?.let{
                       appointmentAdapter.appointments = it
                       appointmentAdapter.notifyDataSetChanged() // notificar que hubo un cambio
                   }

               }
            }

        })
    }
}
