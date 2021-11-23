package com.ch.ni.an.fun_pet_project

import android.view.animation.AnimationUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wolfram.alpha.WAEngine
import com.wolfram.alpha.WAPlainText
import java.lang.StringBuilder

class FunViewModel: ViewModel() {

    private lateinit var waEngine :WAEngine

    private val _state: MutableLiveData<STATE> = MutableLiveData()
    val state: LiveData<STATE> = _state

    private val _data: MutableLiveData<List<AnyItem>> = MutableLiveData()
    val data: LiveData<List<AnyItem>> = _data

    private val listItems: MutableList<AnyItem> = mutableListOf()


    private fun initWolfRamEngine(){
        waEngine = WAEngine().apply {
            appID = API_KEY
            addFormat("plaintext")
        }
    }

    fun askWolfRam(request :String) {
        _state.value = viewModelScope.launch(Dispatchers.IO) {
            val query = waEngine.createQuery().apply { input = request }
            runCatching {
                waEngine.performQuery(query)
            }.onSuccess {
                if (it.isError) {
                    _state.postValue(Error(it.isError.toString()))
                    return@launch
                } else if (!it.isSuccess) {
                    _state.postValue(ErrorRequest())
                    return@launch
                }
                for (i in it.pods) {
                    if (i.isError) continue
                    val content = StringBuilder()
                    for (subI in i.subpods) {
                        for (element in subI.contents) {
                            if (element is WAPlainText) {
                                content.append(element)
                            }
                        }
                    }
                    listItems.add(AnyItem(title = i.title, content = content.toString()))
                    updateList(listItems)
                    _state.postValue(Success())
                }

            }.onFailure {
                _state.postValue(Error(it.message.toString()))
            }
        }
    }



    private fun updateList(items: MutableList<AnyItem>){
        _data.postValue(items)
    }

    init {
        initWolfRamEngine()
    }
}

sealed class STATE {

}
class Pending() : STATE() {}
class Success() : STATE() {}
class Error(e :String) : STATE() {
    fun showError(e :String) :String {
        return e
    }

}
class ErrorRequest : STATE() {}













private const val API_KEY = "VL3TX8-HXP2JWVHQU"