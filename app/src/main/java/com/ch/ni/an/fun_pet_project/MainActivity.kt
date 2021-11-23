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



class MainActivity : AppCompatActivity() {

    private val myModel: FunViewModel by viewModels()

    private lateinit var progressBar :ProgressBar
    private lateinit var resultInput :TextInputEditText
    private lateinit var pods :MutableList<AnyItem>
    private lateinit var recyclerView :RecyclerView
    private lateinit var adapter: FunAdapter


    override fun onCreate(savedInstanceState :Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

                val question = resultInput.text.toString()

            }
            return@setOnEditorActionListener false
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


    }
}