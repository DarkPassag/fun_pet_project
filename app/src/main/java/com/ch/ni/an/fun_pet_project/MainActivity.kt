package com.ch.ni.an.fun_pet_project

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SimpleAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.wolfram.alpha.WAEngine
import com.wolfram.alpha.WAPlainText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.StringBuilder


private const val API_KEY = "VL3TX8-HXP2JWVHQU"
class MainActivity : AppCompatActivity() {

    private lateinit var waEngine :WAEngine
    private lateinit var progressBar :ProgressBar
    private lateinit var resultInput :TextInputEditText
    private lateinit var pods :MutableList<AnyItem>
    private lateinit var recyclerView :RecyclerView
    private lateinit var adapter: FunAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState :Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initWolfFrameEngine()

        progressBar = findViewById(R.id.progressBar)
        resultInput = findViewById(R.id.textInput)
        pods = mutableListOf<AnyItem>()
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FunAdapter()
        adapter.submitList(pods)
        recyclerView.adapter = adapter

        resultInput.setOnEditorActionListener { _, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                pods.clear()
                adapter.notifyDataSetChanged()

                val question = resultInput.text.toString()
                asWolfram(question)
            }
            return@setOnEditorActionListener false
        }


    }



   private fun initWolfFrameEngine(){
        waEngine = WAEngine().apply {
            appID = API_KEY
            addFormat("plaintext")
        }
    }

    private fun showSnackBar(message: String){
        Snackbar.make(
            findViewById(R.id.content),
            message,
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(R.string.ok){
                dismiss()
            }
        }.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun asWolfram(request: String){
        progressBar.visibility= View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val query = waEngine.createQuery().apply { input = request }
            kotlin.runCatching {
                waEngine.performQuery(query)
            }.onSuccess { result ->
                withContext(Dispatchers.Main){
                    progressBar.visibility = View.GONE
                    if(result.isError){
                        showSnackBar(result.errorMessage)
                        return@withContext
                    }
                    if(!result.isSuccess){
                        resultInput.error = getString(R.string.fail_unknowing)
                        return@withContext
                    }

                    for(pod in result.pods) {
                        if (pod.isError) continue
                        val content = StringBuilder()
                        for (i in pod.subpods ){
                            for(element in i.contents){
                                if(element is WAPlainText){
                                    content.append(element.text)
                                }
                            }
                        }
                        pods.add(AnyItem(
                            title = pod.title,
                            content = content.toString()
                        ))
                        adapter.submitList(pods)
                        adapter.notifyDataSetChanged()
                    }


                }

            }.onFailure { t ->
                withContext(Dispatchers.Main){
                    progressBar.visibility = View.GONE
                    showSnackBar(t.message ?: getString(R.string.fail))
                }
            }
        }
    }
}