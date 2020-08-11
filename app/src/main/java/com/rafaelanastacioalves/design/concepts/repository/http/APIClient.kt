package com.rafaelanastacioalves.design.concepts.repository.http;


import com.rafaelanastacioalves.design.concepts.domain.entities.EntityDetails
import com.rafaelanastacioalves.design.concepts.domain.entities.MainEntity
import retrofit2.http.POST
import retrofit2.http.Path

interface APIClient {

    @POST("/trip-packages")
    suspend fun getMainEntityList(): List<MainEntity>;

    @POST("/trip-packages/{entityID}")
    suspend fun getEntityDetails(@Path("entityID") id: String): EntityDetails

    @POST("/trip-packages-additional")
    suspend fun getMainEntityListAdditional(): List<MainEntity>

}
