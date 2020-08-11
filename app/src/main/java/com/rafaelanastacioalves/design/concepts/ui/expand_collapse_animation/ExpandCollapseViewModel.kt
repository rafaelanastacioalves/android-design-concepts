package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelanastacioalves.design.concepts.domain.entities.MainEntity
import com.rafaelanastacioalves.design.concepts.domain.entities.Resource
import com.rafaelanastacioalves.design.concepts.domain.interactors.MainEntityListInteractor


class ExpandCollapseViewModel : ViewModel() {

    val mainEntityListLiveData = MutableLiveData<Resource<List<MainEntity>>>();
    private val mainEntityListInteractor: MainEntityListInteractor = MainEntityListInteractor()



    fun loadDataIfNecessary(){
        if (mainEntityListLiveData.value == null){
            mainEntityListInteractor.execute(viewModelScope,null, {
                handle(it)
            })
        }
    }

    private fun handle(it: Resource<List<MainEntity>>) {
        mainEntityListLiveData.postValue(it)
    }

}
