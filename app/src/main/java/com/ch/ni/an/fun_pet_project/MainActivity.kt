package com.ch.ni.an.fun_pet_project

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SimpleAdapter
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
    private lateinit var pods :MutableList<HashMap<String, String>>
    private lateinit var list :ListView
    private lateinit var adapter :SimpleAdapter

    override fun onCreate(savedInstanceState :Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initWolfFrameEngine()

        progressBar = findViewById(R.id.progressBar)
        resultInput = findViewById(R.id.textInput)
        pods = mutableListOf()
        list = findViewById(R.id.listView)
        adapter = SimpleAdapter(
            this,
            pods,
            R.layout.recyclerview_item,
            arrayOf("Title", "Content"),
            intArrayOf(R.id.titleTextView, R.id.contentTextView)
        )
        list.adapter = adapter

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
                        pods.add(0, HashMap<String, String>().apply {
                            put("Title", pod.title)
                            put("Content", content.toString())
                        })

                    }
                    adapter.notifyDataSetChanged()

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