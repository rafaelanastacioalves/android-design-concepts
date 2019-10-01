package com.example.rafaelanastacioalves.moby.retrofit;


import com.example.rafaelanastacioalves.moby.entities.EntityDetails
import com.example.rafaelanastacioalves.moby.entities.MainEntity
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

interface APIClient {


    @POST("/trip-packages")
    suspend fun getTripPackageList(): List<MainEntity>;

    @POST("/trip-packages/{tripPackageID}")
    fun getTripPackageDetails(@Path("tripPackageID") id: String): Call<EntityDetails>;

}
