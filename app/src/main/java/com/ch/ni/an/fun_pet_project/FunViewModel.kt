package com.ch.ni.an.fun_pet_project


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ch.ni.an.fun_pet_project.PrivateKeys.APPID
import com.wolfram.alpha.WAEngine
import com.wolfram.alpha.WAPlainText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.util.*

class FunViewModel: ViewModel() {

    private lateinit var waEngine :WAEngine

    private val _state: MutableLiveData<STATE> = MutableLiveData()
    val state: LiveData<STATE> = _state

    private val _data: MutableLiveData<List<AnyItem>> = MutableLiveData()
    val data: LiveData<List<AnyItem>> = _data

    private val listItems: MutableList<AnyItem> = mutableListOf()


    private fun initWolfRamEngine(){
        waEngine = WAEngine().apply {
            appID = APPID
            addFormat("plaintext")
        }
    }

    fun askWolfRam(callback :ErrorCallback,request :String, ) {
        _state.value = Pending()
            viewModelScope.launch(Dispatchers.IO) {
            val query = waEngine.createQuery().apply { input = request }
            runCatching {
                waEngine.performQuery(query)
            }.onSuccess {
                if (it.isError) {
                    _state.postValue(Error())
                    callback.provideText(it.errorMessage.toString())
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
                callback.provideText(it.message.toString())
                _state.postValue(Error())
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

class Error() : STATE(){}
class ErrorRequest : STATE() {}











