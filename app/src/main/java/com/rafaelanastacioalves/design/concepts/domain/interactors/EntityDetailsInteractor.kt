package com.rafaelanastacioalves.design.concepts.domain.interactors


import com.rafaelanastacioalves.design.concepts.domain.entities.EntityDetails
import com.rafaelanastacioalves.design.concepts.domain.entities.Resource
import com.rafaelanastacioalves.design.concepts.repository.AppRepository

class EntityDetailsInteractor :
        Interactor<Resource<EntityDetails>?, EntityDetailsInteractor.RequestValues>() {
    val appRepository: AppRepository

    init {
        appRepository = AppRepository
    }

    override suspend fun run(requestValue: RequestValues?): Resource<EntityDetails>? {
        var result = requestValue?.requestId?.let { appRepository.entityDetails(it) }
        return result
    }

    class RequestValues(val requestId: String) : Interactor.RequestValues

}
