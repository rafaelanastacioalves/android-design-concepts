package com.example.rafaelanastacioalves.moby.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rafaelanastacioalves.moby.domain.entities.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.net.HttpURLConnection

abstract class NetworkBoundResource<ResultType, RequestType> {

    val viewModelScope = CoroutineScope(Dispatchers.IO)
    private lateinit var result: Resource<ResultType>

    abstract suspend fun makeCall(): ResultType?

    abstract suspend fun getFromDB(): ResultType?

    abstract fun saveIntoDB(resultData: ResultType?)

    private suspend fun fetchFromNetwork() {
        val resultData: ResultType?
        try {
            resultData = makeCall()
            result = Resource(Resource.Status.SUCCESS, resultData, null)

        } catch (exception: Exception) {
            if (exception is HttpException) {
                treatHttpException(exception)
            } else {
                result = Resource.error(Resource.Status.GENERIC_ERROR,
                        null,
                        null)
            }
        }
    }

    private suspend fun fetchFromNetworkAndDB() {
        val localData: ResultType?
        try {
            localData = getFromDB()
            result = if (localData != null) {
                Resource(Resource.Status.SUCCESS, localData, null)
            } else {
                val resultData: ResultType? = makeCall()
                saveIntoDB(resultData)
                Resource(Resource.Status.SUCCESS, getFromDB(), null)

            }
        } catch (exception: Exception) {
            if (exception is HttpException) {
                treatHttpException(exception)
            } else {
                result = Resource.error(Resource.Status.GENERIC_ERROR,
                        null,
                        null)
            }
        }
    }

    private fun treatHttpException(exception: HttpException) {
        when (exception.code()) {
            HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                result = Resource.error(
                        Resource.Status.INTERNAL_SERVER_ERROR
                        , null
                        , null)
            }

            else -> {
                result = Resource.error(
                        Resource.Status.GENERIC_ERROR,
                        null,
                        null
                )
            }

        }
    }

    suspend fun fromHttpOnly(): Resource<ResultType> {
        fetchFromNetwork()
        return result
    }

    suspend fun fromHttpAndDB(): Resource<ResultType> {
        fetchFromNetworkAndDB()
        return result
    }

    fun asLiveData(): LiveData<Resource<RequestType>> {
        return MutableLiveData()
    }

}