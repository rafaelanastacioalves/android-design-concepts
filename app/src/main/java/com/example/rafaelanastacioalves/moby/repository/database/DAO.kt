package com.example.rafaelanastacioalves.moby.repository.database

import com.example.rafaelanastacioalves.moby.domain.entities.MainEntity
import com.orhanobut.hawk.Hawk

object DAO {
    private const val MAIN_ENTITY_LIST_KEY = "AAAA"

    fun getMainEntityList(): List<MainEntity>? {
        return Hawk.get(MAIN_ENTITY_LIST_KEY)
    }

    fun saveMainEntityList(resultData: List<MainEntity>?) {
        val resultSuccessfull = Hawk.put(MAIN_ENTITY_LIST_KEY, resultData)
        if (!resultSuccessfull) {
            throw Exception()
        }
    }
}