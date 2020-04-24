package com.example.rafaelanastacioalves.moby.repository

import com.example.rafaelanastacioalves.moby.domain.entities.EntityDetails
import com.example.rafaelanastacioalves.moby.domain.entities.MainEntity
import com.example.rafaelanastacioalves.moby.domain.entities.Resource
import com.example.rafaelanastacioalves.moby.repository.database.DAO
import com.example.rafaelanastacioalves.moby.repository.http.APIClient
import com.example.rafaelanastacioalves.moby.repository.http.ServiceGenerator

object AppRepository {

    suspend fun mainEntity(): Resource<List<MainEntity>> {
        return object : NetworkBoundResource<List<MainEntity>, List<MainEntity>>() {
            override suspend fun makeCall(): List<MainEntity>? {

                var apiClient: APIClient = ServiceGenerator.createService(APIClient::class.java);
                return apiClient.getTripPackageList()
            }

            override suspend fun getFromDB(): List<MainEntity>? {
                return DAO.getTripPackageList()
            }

            override fun saveIntoDB(resultData: List<MainEntity>?) {
                DAO.saveTripPackageList(resultData)
            }

        }.fromHttpAndDB()
    }

    suspend fun mainEntityAdditional(): Resource<List<MainEntity>> {
        return object : NetworkBoundResource<List<MainEntity>, List<MainEntity>>() {
            override suspend fun makeCall(): List<MainEntity>? {

                var apiClient: APIClient = ServiceGenerator.createService(APIClient::class.java);
                return apiClient.getTripPackageListAdditional()
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
            override suspend fun makeCall(): EntityDetails? {
                var apiClient: APIClient = ServiceGenerator.createService(APIClient::class.java)
                return apiClient.getTripPackageDetails(requestId)
            }

            override suspend fun getFromDB(): EntityDetails? {
                TODO("Not yet implemented")
            }

            override fun saveIntoDB(resultData: EntityDetails?) {
                TODO("Not yet implemented")
            }
        }.fromHttpOnly()
    }
}