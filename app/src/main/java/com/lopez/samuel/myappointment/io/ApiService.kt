package com.lopez.samuel.myappointment.io

import com.lopez.samuel.myappointment.model.Doctor
import com.lopez.samuel.myappointment.model.Specialty
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("specialties")
    abstract fun getSpecialties(): Call<ArrayList<Specialty>>

    @GET("specialties/{specialty}/doctors")
    abstract fun getDoctors(@Path("specialty") specialtyId: Int): Call<ArrayList<Doctor>>


    companion object Factory{
        private const val BASE_URL = "http://192.168.1.66:8000/api/"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                return retrofit.create(ApiService::class.java)
        }
    }
}