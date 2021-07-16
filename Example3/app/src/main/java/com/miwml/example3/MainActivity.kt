package com.miwml.example3

import android.content.res.AssetManager
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.miwml.example3.databinding.ActivityMainBinding
import com.miwml.example3.network.ModelApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var resultText: TextView
    private lateinit var examplesList: List<String>
    private lateinit var progressBar: ProgressBar
    private lateinit var inputText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Register listeners
        binding.inferButton.setOnClickListener { onClickInfer() }
        binding.generateButton.setOnClickListener { onClickGenerate() }

        // Capture elements form the UI
        resultText = binding.resultText
        progressBar = binding.progressBar
        inputText = binding.inputText

        // Load some examples of sentences
        examplesList = loadLabelList(this.assets, "sentences.txt")

    }

    /**
     * This function populates the list of examples
     */
    private fun loadLabelList(assetManager: AssetManager, labelPath: String): List<String> {
        return assetManager.open(labelPath).bufferedReader().useLines { it.toList() }
    }

    /**
     * When the button to generate is clicked
     */
    private fun onClickGenerate() {
        val text = examplesList[Random.nextInt(0, examplesList.size)].replace("\\n", "\n")
        resultText.text = ""
        inputText.setText(text)
    }

    /**
     * This function serves to call the REST API
     */
    private suspend fun getModelOutput(input: String) {
        try {
            val listResult = ModelApi.retrofitService.getModelOutput(input)
            handleResponseSuccess(listResult.output)
        } catch (e: Exception) {
            handleResponseError()
        }
    }

    /**
     * When the button infer is clicked
     */
    private fun onClickInfer() {
        if (inputText.text.toString().trim().isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            resultText.text = ""
            GlobalScope.launch {
                getModelOutput(inputText.text.toString())
            } // This calls the function in a different thread from the UI. This serves to not freeze the UI of the user.
        }

    }

    /**
     * This function serves to display the result of the model
     */
    private fun handleResponseSuccess(output: String) {
        GlobalScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.INVISIBLE
            resultText.text = output
        } // This uses the main thread to display the result
    }

    /**
     * This function serves to display the some error during the API call
     */
    private fun handleResponseError() {
        GlobalScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.INVISIBLE
            resultText.text = getString(R.string.Error)
        }
    }

}