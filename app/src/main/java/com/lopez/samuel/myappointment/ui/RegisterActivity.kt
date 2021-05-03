package com.lopez.samuel.myappointment.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.lopez.samuel.myappointment.R
import com.lopez.samuel.myappointment.io.ApiService
import com.lopez.samuel.myappointment.io.response.LoginResponse
import com.lopez.samuel.myappointment.util.PreferenceHelper
import com.lopez.samuel.myappointment.util.PreferenceHelper.set
import com.lopez.samuel.myappointment.util.toast
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    val apiService by lazy{
        ApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegistrarUsuario.setOnClickListener {
            perfomRegigster()
        }

        tvGoToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)


        }

    }

    private fun perfomRegigster(){
        val name = etRegisterName.text.toString().trim()
        val email = etRegisterEmail.text.toString().trim()
        val password = etRegisterPassword.text.toString()
        val confirmedPassword = etConfirmedPassword.text.toString()

        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmedPassword.isEmpty()){
            toast(getString(R.string.error_please_empy_fields))
            return
        }

        if(password != confirmedPassword){
            toast(getString(R.string.error_Coincided_passwords))
            return
        }

        val call = apiService.postRegister(email,name,password,confirmedPassword)
        call.enqueue(object: Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toast(t.localizedMessage)
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    val loginResponse = response.body()



                    if(loginResponse == null){
                        toast(getString(R.string.error_login_response))
                        return
                    }

                    if(loginResponse.success){
                        createSessionPreference(loginResponse.access_token)
                        toast(getString(R.string.welcome_name, loginResponse.user.name))
                        goToMenuActivity()
                    }else{
                        toast(getString(R.string.credential_incorrect))
                    }
                }else{
                    toast(getString(R.string.error_register_user))
                }
            }

        })

    }


    private fun createSessionPreference(access_token: String){
        /*
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session",true)
        editor.apply()
        */
        val preferences = PreferenceHelper.defaultPrefs(this)
        preferences["access_token"] = access_token

    }

    private fun goToMenuActivity(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}
