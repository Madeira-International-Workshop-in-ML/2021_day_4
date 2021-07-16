package com.miwml.example1

import android.content.res.AssetManager
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.miwml.example1.databinding.ActivityMainBinding
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var interpreter: Interpreter
    private lateinit var modelPath: String
    private lateinit var inputText: EditText
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Register listeners
        binding.calculateButton.setOnClickListener { computeResult() }

        // Capture elements form the UI
        inputText = binding.inputText
        resultText = binding.resultText

        // Load the model and set options
        modelPath = "model.tflite"
        val options = Interpreter.Options()
        options.setNumThreads(5)
        interpreter = Interpreter(loadModelFile(assets), options)

    }

    /**
     * This function selects the input for the network and shows the result of the inference
     */
    private fun computeResult() {
        // Get the text from the text view and convert it to string
        val inputNumber = inputText.text.toString()

        // If no information was input, ignore
        if (inputNumber == "") {
            resultText.text = ""
            return
        }

        // Otherwise, compute and show the result
        val result = infer(inputNumber).toString()
        resultText.text = getString(R.string.result, result)
    }

    /**
     * This function computes the output of the network
     */
    private fun infer(inputString: String): Float {
        // Converts the string to JAVA FLOAT
        val inputVal = FloatArray(1)
        inputVal[0] = java.lang.Float.valueOf(inputString)

        // Prepare the output
        val output = Array(1) { FloatArray(1) }

        // Compute the inference
        interpreter.run(inputVal, output)
        return output[0][0]
    }

    /**
     * This function load the SavedModel
     */
    private fun loadModelFile(assetManager: AssetManager): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

}