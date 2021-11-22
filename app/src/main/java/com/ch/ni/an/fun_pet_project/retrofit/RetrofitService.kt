package com.ch.ni.an.fun_pet_project.retrofit

import retrofit2.Response
import retrofit2.http.GET

interface RetrofitService{

    @GET("")
    suspend fun getResponse():Response<Any>
}


object Common{

    private const val BASE_URL = "http://api.wolframalpha.com/v1/result"

    val retrofit = RetrofitClient.getClient("").create(RetrofitService::class.java)
}