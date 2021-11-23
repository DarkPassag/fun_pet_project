package com.ch.ni.an.fun_pet_project


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText




class MainActivity : AppCompatActivity(), ErrorCallback {

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

        myModel.state.observe(this, { updateUI(it) })

        myModel.data.observe(this, {
            adapter.submitList(it)

        })

        resultInput.setOnEditorActionListener { _, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                pods.clear()
                val question = resultInput.text.toString()
                if(question.isNotEmpty()) myModel.askWolfRam(this,question)
                else showSnackBar(getString(R.string.input_text))
            }
            return@setOnEditorActionListener false
        }


    }


    private fun showSnackBar(message :String) {
        Snackbar.make(
            findViewById
                (R.id.content), message, Snackbar.LENGTH_INDEFINITE).apply {
            setAction(R.string.ok
            )
            {
                dismiss()
            }
        }.show()
    }

    private fun updateUI(state :STATE){
        when(state){
            is ErrorRequest -> stateErrorRequest()
            is Error -> stateUnPending()
            is Success -> stateUnPending()
            is Pending -> statePending()
        }
    }
    private fun statePending(){
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun stateUnPending(){
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun stateErrorRequest(){
//        val request = resultInput.text.toString()
        resultInput.error = getString(R.string.fail_unknowing)
        stateUnPending()
    }

    override fun provideText(e :String) {
        showSnackBar(e)
    }


}