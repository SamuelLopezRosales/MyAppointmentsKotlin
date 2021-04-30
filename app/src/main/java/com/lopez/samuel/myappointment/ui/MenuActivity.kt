package com.lopez.samuel.myappointment.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.lopez.samuel.myappointment.util.PreferenceHelper
import com.lopez.samuel.myappointment.util.PreferenceHelper.set
import com.lopez.samuel.myappointment.util.PreferenceHelper.get
import com.lopez.samuel.myappointment.R
import com.lopez.samuel.myappointment.io.ApiService
import com.lopez.samuel.myappointment.util.toast
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {

    private val apiService by lazy{
        ApiService.create()
    }

    private val preferences by lazy{
        PreferenceHelper.defaultPrefs(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        btnCreateAppointment.setOnClickListener {
            val intent = Intent(this, CreateAppointmentActivity::class.java)
            startActivity(intent)
        }

        btnMyAppointments.setOnClickListener {
            val intent = Intent(this, AppointmentsActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            performLogout()
            clearSessionPreference()

        }
    }

    private fun performLogout(){
        val access_token = preferences["access_token",""]
        val call = apiService.postLogout("Bearer $access_token")
        call.enqueue(object: Callback<Void>{
            override fun onFailure(call: Call<Void>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                clearSessionPreference()
                val intent = Intent(this@MenuActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        })
    }

    private fun clearSessionPreference(){
        /*val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session",false)
        editor.apply()*/

        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["access_token"] = ""


    }
}
