package com.lopez.samuel.myappointment.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import com.lopez.samuel.myappointment.R
import com.lopez.samuel.myappointment.io.ApiService
import com.lopez.samuel.myappointment.model.Specialty
import kotlinx.android.synthetic.main.activity_create_appointment.*
import kotlinx.android.synthetic.main.cardview_step1.*
import kotlinx.android.synthetic.main.cardview_step2.*
import kotlinx.android.synthetic.main.cardview_step3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CreateAppointmentActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy{
        ApiService.create()
    }

    val selectedCalendar = Calendar.getInstance()
    private var selectedRadioButton: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_appointment)

        btnNext.setOnClickListener {
            if(etDescription.text.toString().length < 3){
                etDescription.error = getString(R.string.validate_appointment_description)
            }else{
                cvStep1.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
        }

        btnNext2.setOnClickListener {
            when {
                etScheduledDate.text.toString().isEmpty() -> etScheduledDate.error = getString(R.string.validate_scheduled_date)
                selectedRadioButton == null -> Snackbar.make(createAppointmentLinearLayout, R.string.validate_appointment_time,Snackbar.LENGTH_SHORT).show()
                else -> {
                    showAppointmentDataToConfirm()
                    cvStep2.visibility = View.GONE
                    cvStep3.visibility = View.VISIBLE
                }
            }
        }

        btnConfirmAppointment.setOnClickListener {
            Toast.makeText(this, "La cita fue registrada correctamente", Toast.LENGTH_SHORT).show()
            finish()
        }


        loadSpecialties()



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
            etScheduledDate.error = null
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

    private fun loadSpecialties(){
        val call = apiService.getSpecialties()
        call.enqueue(object: Callback<ArrayList<Specialty>>{
            override fun onFailure(call: Call<ArrayList<Specialty>>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity,getString(R.string.error_loading_speialties), Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onResponse(call: Call<ArrayList<Specialty>>, response: Response<ArrayList<Specialty>>) {
                if(response.isSuccessful){ // cuando es correcto esta entre 200 y 300
                    val specialties = response.body() // obtenemos un arrayList de especialidades

                    val specialyOptions = ArrayList<String>()
                    specialties?.forEach{
                        specialyOptions.add(it.name)
                    }
                    spinnerSpecialties.adapter = ArrayAdapter<String>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1,specialyOptions)
                }
            }

        })


    }

    private fun showAppointmentDataToConfirm(){
        tvConfirmDescription.text = etDescription.text.toString()

        val selectedRadioBtnId = radioGrupType.checkedRadioButtonId
        val selectedRadioType = radioGrupType.findViewById<RadioButton>(selectedRadioBtnId)
        tvConfirmType.text  = selectedRadioType.text.toString()

        tvConfirmSpecialty.text = spinnerSpecialties.selectedItem.toString()
        tvConfirmDoctor.text = spinnerDoctors.selectedItem.toString()
        tvConfirmScheduledDate.text = etScheduledDate.text.toString()
        tvConfirmScheduledTime.text = selectedRadioButton?.text.toString()
    }

    override fun onBackPressed() {
        when {
            cvStep3.visibility == View.VISIBLE -> {
                cvStep3.visibility = View.GONE
                cvStep2.visibility = View.VISIBLE
            }
            cvStep2.visibility == View.VISIBLE -> {
                cvStep2.visibility = View.GONE
                cvStep1.visibility = View.VISIBLE
            }
            cvStep1.visibility == View.VISIBLE -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("¿Estas seguro que deseas salir?")
                builder.setMessage("Si abandonas el registro, los datos que has escrito se perderan")
                builder.setPositiveButton("Si, Salir") { _, _ ->
                    finish()

                }
                builder.setNegativeButton("Continuar registro"){ dialog, _->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }
        }

    }

    // extencion fucntion
    //fun Int.twoDigits(): String
            //= if(this>9) this.toString() else "0$this"

    private fun Int.twoDigits()
            = if(this>=10) this.toString() else "0$this"

}
