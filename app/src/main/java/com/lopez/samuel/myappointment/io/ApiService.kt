package com.lopez.samuel.myappointment.io

import com.lopez.samuel.myappointment.io.response.LoginResponse
import com.lopez.samuel.myappointment.model.Doctor
import com.lopez.samuel.myappointment.model.Schedule
import com.lopez.samuel.myappointment.model.Specialty
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @GET("specialties")
    abstract fun getSpecialties(): Call<ArrayList<Specialty>>

    @GET("specialties/{specialty}/doctors")
    abstract fun getDoctors(@Path("specialty") specialtyId: Int): Call<ArrayList<Doctor>>

    @GET("schedule/hours")
    abstract fun getHours(@Query("doctor_id") doctorId: Int, @Query("date") date: String): Call<Schedule>

    @POST("login")
    abstract fun postLogin(@Query("email") email: String, @Query("password") password: String):
            Call<LoginResponse>

    @POST("logout")
    abstract fun postLogout(@Header("Authorization") authHeader: String): Call<Void>


    companion object Factory{
        private const val BASE_URL = "http://192.168.1.66:8000/api/"

        fun create(): ApiService {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                return retrofit.create(ApiService::class.java)
        }
    }
}