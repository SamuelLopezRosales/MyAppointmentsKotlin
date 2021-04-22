package com.lopez.samuel.myappointment

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_appointment.*
import java.util.*

class CreateAppointmentActivity : AppCompatActivity() {
    val selectedCalendar = Calendar.getInstance()
    private var selectedRadioButton: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        btnNext.setOnClickListener {
            cvStep1.visibility = View.GONE
            cvStep2.visibility = View.VISIBLE
        }

        btnConfirmAppointment.setOnClickListener {
            Toast.makeText(this, "La cita fue registrada correctamente", Toast.LENGTH_SHORT).show()
            finish()
        }

        // el material spinner arrayAdapter nos pide 3 parametros(contexto, de que forma se mostraran los datos
        // y finalmente el arreglo de datos)
        val specialtiesOptions = arrayOf("Specialty A","Specialty B","Specialty C")
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,specialtiesOptions)
        spinnerSpecialties.setAdapter(adapter)


        val doctorOptions = arrayOf("Medico 1","Medico 2","Medico 3")
        spinnerDoctors.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,doctorOptions)


    }

    fun onClickScheduledDate(v: View){ // creo objeto calendario
        // obtener el año, mes y dia del mes actual
        // val calenadar = Calendar.getInstance()
        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        // Esto pasa cuando selecciono un dia //
        val listener = DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
            //Toast.makeText(this,"$y-$m-$d", Toast.LENGTH_SHORT).show()
            selectedCalendar.set(y,m,d)
            etScheduledDate.setText(resources.getString(R.string.date_format,
                    y,
                    m.twoDigits(),
                    d.twoDigits()
                )
            )
            displayRadioButtons()
        }
        // creo un dialogoPicker
        val datePieckerDialog = DatePickerDialog(this, listener, year, month, dayOfMonth)
        val datePicker = datePieckerDialog.datePicker

        // otro calendar para validacion de maximo y minimo
        val calendar = Calendar.getInstance() // dia actual
        // dumarle un dia
        calendar.add(Calendar.DAY_OF_MONTH,1)
        datePicker.minDate = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH,29)
        datePicker.maxDate = calendar.timeInMillis


        datePieckerDialog.show()
    }

    private fun displayRadioButtons(){
        // limpiar los radioButton ya existentes
        //radioGroup.clearCheck()
        selectedRadioButton = null
        // limpiar los elementos ya puestos
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()

        val hours = arrayOf("3:00 PM","3:30 PM","4:00 PM", "4:30 PM")
        var goToLeft = true

        hours.forEach {
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()
            radioButton.text = it

            // desmarcar los radio buton y marcar al que hizimos click
            radioButton.setOnClickListener{ view->
                selectedRadioButton?.isChecked = false
                selectedRadioButton = view as RadioButton?
                selectedRadioButton?.isChecked = true
            }
            if(goToLeft)
                radioGroupLeft.addView(radioButton)
            else
                radioGroupRight.addView(radioButton)
            goToLeft = !goToLeft
        }

        // obtener el id del radioButton Marcado
        //radioGroup.checkedRadioButtonId

    }


    // extencion fucntion
    //fun Int.twoDigits(): String
            //= if(this>9) this.toString() else "0$this"

    private fun Int.twoDigits()
            = if(this>=10) this.toString() else "0$this"

}
