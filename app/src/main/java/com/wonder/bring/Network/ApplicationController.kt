package com.wonder.bring.Network

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApplicationController : Application() {

    private val baseURL="http://bomi.gq:8080"
    lateinit var networkService: NetworkService

    companion object {
        lateinit var instance: ApplicationController
    }

    override fun onCreate() {
        super.onCreate()
        instance=this   // 싱글톤으로 만든 instance변수에 본인이 들어가고,
        buildNetwork()
    }

    fun buildNetwork(){
        val retrofit:Retrofit=Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        networkService=retrofit.create(NetworkService::class.java)          //
    }
}