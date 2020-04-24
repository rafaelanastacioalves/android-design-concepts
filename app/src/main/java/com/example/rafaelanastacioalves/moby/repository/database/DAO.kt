package com.example.rafaelanastacioalves.moby.repository.database

import com.example.rafaelanastacioalves.moby.domain.entities.MainEntity
import com.orhanobut.hawk.Hawk

object DAO {
    private const val TRIP_PACKAGE_LIST_KEY = "AAAA"

    fun getTripPackageList(): List<MainEntity>? {
        return Hawk.get(TRIP_PACKAGE_LIST_KEY)
    }

    fun saveTripPackageList(resultData: List<MainEntity>?) {
        val resultSuccessfull = Hawk.put(TRIP_PACKAGE_LIST_KEY, resultData)
        if (!resultSuccessfull) {
            throw Exception()
        }
    }
}