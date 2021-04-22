package com.lopez.samuel.myappointment

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import com.lopez.samuel.myappointment.PreferenceHelper.get
import com.lopez.samuel.myappointment.PreferenceHelper.set

class MainActivity : AppCompatActivity() {

    private val snackbar by lazy { // significa que si se requiere se llama sino permanecera nula
        Snackbar.make(mainLayout,R.string.press_back_again, Snackbar.LENGTH_SHORT)
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

            if(preferences["session",false])
                goToMenuActivity()

            btnLogin.setOnClickListener {
                // validar en servidor
                createSessionPreference()
                goToMenuActivity()
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

        private fun createSessionPreference(){
            /*
            val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putBoolean("session",true)
            editor.apply()
            */
            val preferences = PreferenceHelper.defaultPrefs(this)
            preferences["session"] = true

        }

        private fun goToMenuActivity(){
            val intent = Intent(this,MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

