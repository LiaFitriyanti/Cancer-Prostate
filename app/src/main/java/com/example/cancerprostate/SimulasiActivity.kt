package com.example.cancerprostate

import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "cancer-prostate.tflite"

    private lateinit var resultText: TextView
    private lateinit var Radius: EditText
    private lateinit var Texture: EditText
    private lateinit var Perimeter: EditText
    private lateinit var Area: EditText
    private lateinit var Smoothness: EditText
    private lateinit var Compactness: EditText
    private lateinit var Symmetry: EditText
    private lateinit var Fractal_Dimension: EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi)

        resultText = findViewById(R.id.txtResult)
        Radius = findViewById(R.id.Radius)
        Texture = findViewById(R.id.Texture)
        Perimeter = findViewById(R.id.Perimeter)
        Area = findViewById(R.id.Area)
        Smoothness = findViewById(R.id.Smoothness)
        Compactness = findViewById(R.id.Compactness)
        Symmetry = findViewById(R.id.Symmetry)
        Fractal_Dimension = findViewById(R.id.FractalDimension)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                Radius.text.toString(),
                Texture.text.toString(),
                Perimeter.text.toString(),
                Area.text.toString(),
                Smoothness.text.toString(),
                Compactness.text.toString(),
                Symmetry.text.toString(),
                Fractal_Dimension.text.toString()
            )
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Terkena Kanker Prostat Jinak"
                }else if (result == 1){
                    resultText.text = "Terkena Kanker Prostat Ganas"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(8)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String, input8: String): Int{
        val inputVal = FloatArray(8)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        inputVal[7] = input8.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}