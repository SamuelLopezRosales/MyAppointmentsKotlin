package com.lopez.samuel.myappointment.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import com.lopez.samuel.myappointment.R
import com.lopez.samuel.myappointment.io.ApiService
import com.lopez.samuel.myappointment.io.response.SimpleResponse
import com.lopez.samuel.myappointment.model.Doctor
import com.lopez.samuel.myappointment.model.Schedule
import com.lopez.samuel.myappointment.model.Specialty
import com.lopez.samuel.myappointment.util.PreferenceHelper
import com.lopez.samuel.myappointment.util.PreferenceHelper.get
import com.lopez.samuel.myappointment.util.toast
import kotlinx.android.synthetic.main.activity_create_appointment.*
import kotlinx.android.synthetic.main.cardview_step1.*
import kotlinx.android.synthetic.main.cardview_step2.*
import kotlinx.android.synthetic.main.cardview_step3.*
import kotlinx.android.synthetic.main.item_appointment.*
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

    private val preferences by lazy{
        PreferenceHelper.defaultPrefs(this)
    }

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
            performStoreAppointment()
        }


        loadSpecialties()
        listenSpecialtyCHange()



        // escuchar cambios segun el medico y segun la fecha
        listenDoctorAndDateChange()


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
                    (m+1).twoDigits(),
                    d.twoDigits()
                )
            )
            etScheduledDate.error = null
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

    private fun displayRadioButtons(hours: ArrayList<String>){
        // limpiar los radioButton ya existentes
        //radioGroup.clearCheck()
        selectedRadioButton = null
        // limpiar los elementos ya puestos
        radioGroupLeft.removeAllViews()
        radioGroupRight.removeAllViews()

        if(hours.isEmpty()){
            tvNotAvailableHours.visibility = View.VISIBLE
            return
        }

        tvNotAvailableHours.visibility = View.GONE

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

                    spinnerSpecialties.adapter = ArrayAdapter<Specialty>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1,specialties)
                }
            }

        })


    }

    private fun loadDoctors(SpecialtyId: Int){
        val call = apiService.getDoctors(SpecialtyId)
        call.enqueue(object: Callback<ArrayList<Doctor>>{
            override fun onFailure(call: Call<ArrayList<Doctor>>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity,getString(R.string.error_loading_doctors), Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<ArrayList<Doctor>>, response: Response<ArrayList<Doctor>>) {
                if(response.isSuccessful){ // cuando es correcto esta entre 200 y 300
                    val doctors = response.body() // obtenemos un arrayList de especialidades

                    spinnerDoctors.adapter = ArrayAdapter<Doctor>(this@CreateAppointmentActivity, android.R.layout.simple_list_item_1,doctors)
                }
            }

        })

    }


    private fun listenSpecialtyCHange(){
        // obtengo el id del spinner Specialties
        spinnerSpecialties.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // adapter nos permitira ingresar a los id
                val specialty = adapter?.getItemAtPosition(position) as Specialty
                //Toast.makeText(this@CreateAppointmentActivity, "Id: ${specialty.id}", Toast.LENGTH_SHORT).show()
                loadDoctors(specialty.id)
            }

        }
    }

    private fun listenDoctorAndDateChange(){
        // medicos
        spinnerDoctors.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // en caso de no tener ningun elemento seleccionado
            }

            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val doctor = adapter?.getItemAtPosition(position) as Doctor
                loadHours(doctor.id, etScheduledDate.text.toString())
            }

        }

        // fechas
        etScheduledDate.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                // DESPUES DE QUE EL TEXTO CAMBIO
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // ANTES DE QUE EL TEXTO CAMBIO
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // CUANDO EL TEXTO CAMBIO
                val doctor = spinnerDoctors.selectedItem as Doctor
                loadHours(doctor.id, etScheduledDate.text.toString())
            }

        })

    }

    private fun loadHours(doctorId: Int, date: String){
        if(date.isEmpty()){
            return
        }

        val call = apiService.getHours(doctorId,date)
        call.enqueue(object: Callback<Schedule>{
            override fun onFailure(call: Call<Schedule>, t: Throwable) {
                Toast.makeText(this@CreateAppointmentActivity, getString(R.string.error_loading_hours), Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<Schedule>, response: Response<Schedule>) {
                if(response.isSuccessful){
                    val schedule = response.body()
                    schedule?.let{

                        tvSelectDoctorAndDate.visibility = View.GONE
                        val intervals = it.morning + it.afternoon
                        val hours = ArrayList<String>()
                        intervals.forEach{interval ->
                            hours.add(interval.start)
                        }
                        displayRadioButtons(hours)
                    }

                    //Toast.makeText(this@CreateAppointmentActivity,"Morning: ${schedule?.morning?.size} - Afternoon: ${schedule?.afternoon?.size}", Toast.LENGTH_SHORT).show()
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

    private fun performStoreAppointment(){
        btnConfirmAppointment.isClickable = false

        val access_token = preferences["access_token",""]
        val authHeader = "Bearer $access_token"
        val description = tvConfirmDescription.text.toString()
        val specialty = spinnerSpecialties.selectedItem as Specialty
        val specialtyId = specialty.id
        val doctor = spinnerDoctors.selectedItem as Doctor
        val doctorId = doctor.id
        val scheduledDate = tvConfirmScheduledDate.text.toString()
        val scheduledTime = tvConfirmScheduledTime.text.toString()
        val type = tvConfirmType.text.toString()

        val call = apiService.storeAppointments(authHeader, description,
                specialtyId, doctorId,
                scheduledDate, scheduledTime,
                type)

        call.enqueue(object: Callback<SimpleResponse>{
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                toast(t.localizedMessage)
                btnConfirmAppointment.isClickable = true
            }

            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                if(response.isSuccessful){
                    toast(getString(R.string.appointment_create_success))
                    finish()
                }else{
                    toast(getString(R.string.Error_register_Appointment))
                    btnConfirmAppointment.isClickable = true
                }
            }

        })

    }

    // extencion fucntion
    //fun Int.twoDigits(): String
            //= if(this>9) this.toString() else "0$this"

    private fun Int.twoDigits()
            = if(this>=10) this.toString() else "0$this"

}
