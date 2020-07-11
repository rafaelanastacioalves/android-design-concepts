package com.example.rafaelanastacioalves.moby.repository

import com.example.rafaelanastacioalves.moby.domain.entities.EntityDetails
import com.example.rafaelanastacioalves.moby.domain.entities.MainEntity
import com.example.rafaelanastacioalves.moby.domain.entities.Resource
import com.example.rafaelanastacioalves.moby.repository.database.AppDataBase
import com.example.rafaelanastacioalves.moby.repository.database.DAO
import com.example.rafaelanastacioalves.moby.repository.http.APIClient
import com.example.rafaelanastacioalves.moby.repository.http.ServiceGenerator

object AppRepository {
    private val appDao: DAO = AppDataBase.getInstance().appDAO()
    var apiClient: APIClient = ServiceGenerator.createService(APIClient::class.java);

    suspend fun mainEntity(): Resource<List<MainEntity>> {
        return object : NetworkBoundResource<List<MainEntity>, List<MainEntity>>() {
            override suspend fun makeCall(): List<MainEntity> {

                return apiClient.getMainEntityList()
            }

            override suspend fun getFromDB(): List<MainEntity>? {
                val mainEntityList = appDao.getMainEntityList()
                return if(mainEntityList.isNotEmpty()){
                    mainEntityList
                }else{
                    null
                }
            }

            override fun saveIntoDB(resultData: List<MainEntity>?) {
                appDao.saveMainEntityList(resultData)
            }

        }.fromHttpAndDB()
    }

    suspend fun mainEntityAdditional(): Resource<List<MainEntity>> {
        return object : NetworkBoundResource<List<MainEntity>, List<MainEntity>>() {
            override suspend fun makeCall(): List<MainEntity> {
                return apiClient.getMainEntityListAdditional()
            }

            override suspend fun getFromDB(): List<MainEntity>? {
                TODO("Not yet implemented")
            }

            override fun saveIntoDB(resultData: List<MainEntity>?) {
                TODO("Not yet implemented")
            }

        }.fromHttpOnly()
    }

    suspend fun entityDetails(requestId: String): Resource<EntityDetails> {
        return object : NetworkBoundResource<EntityDetails, EntityDetails>() {
            override suspend fun makeCall(): EntityDetails {
                return apiClient.getEntityDetails(requestId)
            }

            override suspend fun getFromDB(): EntityDetails? {
                TODO("Not yet implemented")
            }

            override fun saveIntoDB(request: EntityDetails?) {
                TODO("Not yet implemented")
            }
        }.fromHttpOnly()
    }
}