package com.lopez.samuel.myappointment.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.widget.Toast
import com.lopez.samuel.myappointment.util.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*
import com.lopez.samuel.myappointment.util.PreferenceHelper.get
import com.lopez.samuel.myappointment.util.PreferenceHelper.set
import com.lopez.samuel.myappointment.R
import com.lopez.samuel.myappointment.io.ApiService
import com.lopez.samuel.myappointment.io.response.LoginResponse
import com.lopez.samuel.myappointment.util.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private val snackbar by lazy { // significa que si se requiere se llama sino permanecera nula
        Snackbar.make(mainLayout, R.string.press_back_again, Snackbar.LENGTH_SHORT)
    }

    private val apiService by lazy{
        ApiService.create()
    }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            // variable que identifique si hay una sesion
            // shared Preferences key => value
            // Sqlite => Tabla de datos
            // file => imagenes

            //val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
            //val session = preferences.getBoolean("session",false)

            val preferences = PreferenceHelper.defaultPrefs(this) // va ser por default

            if(preferences["access_token",""].contains("."))
                goToMenuActivity()

            btnLogin.setOnClickListener {
                // validar en servidor
                performLogin()


            }

            tvGoToRegister.setOnClickListener {
                Toast.makeText(this, getString(R.string.please_fill_your_data), Toast.LENGTH_SHORT)

                // si usamos var la variable puede cambiar pero si usamos val no cambiara
                // para no tener error tenemos que poner .java al final porque intent es una clase java
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)

            }



        }

        override fun onBackPressed() {
            if(snackbar.isShown)
                super.onBackPressed()
            else
                snackbar.show()
        }

        private fun performLogin(){
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if(email.trim().isEmpty() || password.trim().isEmpty()){
                toast(getString(R.string.please_outpu_))
                return
            }


            val call = apiService.postLogin(etEmail.text.toString(), etPassword.text.toString())
            call.enqueue(object: Callback<LoginResponse>{
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
                        toast(getString(R.string.error_login_response))
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

