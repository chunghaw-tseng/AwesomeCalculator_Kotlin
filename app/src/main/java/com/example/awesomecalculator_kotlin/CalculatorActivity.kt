package com.example.awesomecalculator_kotlin

import android.opengl.ETC1.getHeight
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.awesomecalculator_kotlin.adapters.CalculatorAdapter
import com.example.awesomecalculator_kotlin.callbacks.btnCallbacks


// TODO Use GridLayout and not Recycler layout
class CalculatorActivity : AppCompatActivity(), btnCallbacks {
    val TAG:String = "CalculatorMain"
    // List of all the keys that we want
    val Verticalkeys:List<String> = listOf(
        "AC",
        "CE",
        "7",
        "8",
        "9",
        "/",
        "6",
        "5",
        "4",
        "x",
        "3",
        "2",
        "1",
        "-",
        "0",
        ".",
        "=",
        "+",
    )
    // INIT VALUE
    private var calculate = mutableListOf<String>("")
    private var showResult:Boolean = false

    private lateinit var calculatorText: TextView
    private lateinit var resultText: TextView
    private var historyActionList: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Starting app")
        init()
    }

    private fun init(){
        // Start the initialization of the activity
        val recyclerView:RecyclerView = findViewById(R.id.keys_grid)
        calculatorText = findViewById(R.id.calculationText)
        resultText = findViewById(R.id.resultText)
        // TODO Calculate the size for each element
        val glm = GridLayoutManager(this, 4)
        glm.setSpanSizeLookup(object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (Verticalkeys.get(position) == "AC" || Verticalkeys.get(position) == "CE") 2 else 1
            }
        })
//        glm.checkLayoutParams(object : RecyclerView.LayoutParams() {
//            fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
//                // force height of viewHolder here, this will override layout_height from xml
//                lp.height = getHeight() / 3
//                return true
//            }
//        })
        recyclerView.layoutManager = glm
        val recyclerViewAdapter = CalculatorAdapter(this, Verticalkeys)
        recyclerView.adapter = recyclerViewAdapter

//        recyclerViewAdapter.setKeys(populateData())
    }

    // TODO allow the - value at the start
    // TODO Replace values
    override fun addCommands(cmd: String) {
        // TODO Add the value to the result when ever the boolean is true
        if (showResult){
            calculatorText.text = "Ans = ${calculate[0]}"
            showResult = false
        }

        var currentValue: String = if (resultText.text.toString() == "0") "" else resultText.text.toString()
        var last = calculate[calculate.size - 1]

        //
        when(cmd) {
            "+", "-", "x", "/" -> {
                // TODO Rewrite
                if (calculate.last() == "" && calculate.size >= 2){
                    if (calculate[calculate.size-2] == "+" || calculate[calculate.size-2] == "-" ||calculate[calculate.size-2] == "/" ||calculate[calculate.size-2] == "x"){
                        // Replace
                        calculate[calculate.size -2] = cmd
                        resultText.text = currentValue.replaceRange(currentValue.length -2, currentValue.length, "$cmd ")
                    }
                }else if(calculate.last() == ""){
                    if (cmd == "-"){
                        calculate[calculate.size-1] = "-"
                        resultText.text = "$currentValue-"
                    }else{
                        calculate[calculate.size - 1] = "0"
                        calculate.add(cmd)
                        calculate.add("")
                        resultText.text = "$currentValue 0 $cmd "
                    }
                } else{
                    resultText.text = "$currentValue $cmd "
                    calculate.add(cmd)
                    calculate.add("")
                    }
            }"." -> {
                if (last == ""){
                    calculate[calculate.size -1] = "0$cmd"
                }else if(last == "-"){
                    calculate[calculate.size -1] = "-0$cmd"
                }
                else{
                    calculate[calculate.size - 1] = last + cmd
                }
            resultText.text = "$currentValue$cmd"

        }else -> {
                // Add value to the list of numbers
                calculate[calculate.size - 1] = last + cmd
                resultText.text = "$currentValue$cmd"
            }
        }
    }

    // TODO Calculate if there is a working function
    override fun calculate() {
        //Check if there is a sign at the end of the function
        if (calculate[calculate.size -1] == "") {
            if (calculate[calculate.size - 2] == "+" || calculate[calculate.size - 2] == "-" || calculate[calculate.size - 2] == "/" || calculate[calculate.size - 2] == "*") {
                calculate.removeAt(calculate.size - 2)
                calculate.removeAt(calculate.size - 1)
                resultText.text = resultText.text.toString().dropLast(3)
            }
        }
        // Divide + Multiply
        do{
            for (i in calculate.indices){
                when(calculate[i]){
                    "x" -> {
                        Log.i(TAG, "Multiply")
                        calculate[i + 1] = (calculate[i -1].toDouble() * calculate[i+1].toDouble()).toString()
                        // Remove elements
                        calculate[i] = ""
                        calculate[i-1] = ""

                    }
                    "/" -> {
                        Log.i(TAG, "Divide")
                        calculate[i +1] = (calculate[i -1].toDouble() / calculate[i+1].toDouble()).toString()
                        // Remove elements
                        calculate[i] = ""
                        calculate[i-1] = ""

                    }
                }
            }
        }while(calculate.contains("x") || calculate.contains("/"))

        // Remove all the empty strings
        calculate.removeAll(listOf(""))
        // Add + Substract
        do{
            calculate.forEachIndexed { i, e ->
                Log.i(TAG, calculate.toString())
                when(e){
                    "+" -> {
                        Log.i(TAG, "Add")
                        calculate[i +1] = (calculate[i -1].toDouble() + calculate[i+1].toDouble()).toString()
                        calculate[i] = ""
                        calculate[i-1] = ""
                    }
                    "-" -> {
                        Log.i(TAG, "Substract")
                        calculate[i +1] = (calculate[i -1].toDouble() - calculate[i+1].toDouble()).toString()
                        calculate[i] = ""
                        calculate[i-1] = ""
                    }
                }
            }
        }while(calculate.contains("+") || calculate.contains("-"))


        calculatorText.text = "${resultText.text} = "
        calculate.removeAll(listOf(""))
        // Return int or double
        resultText.text = if((calculate[0].toDouble() % 1).equals(0.0)) (calculate[0].toDouble()).toInt().toString() else calculate[0]
        showResult = true

    }

    // TODO the 0 get deleted
    override fun deleteCommands() {
        val currentText = resultText.text.toString()
        if (currentText != "0"){
            calculate.add("")
            var replace = if(currentText.last().isWhitespace())currentText.dropLast(3) else currentText.dropLast(1)
            if (replace.isEmpty()){
                resultText.text = "0"
            }else{
                resultText.text = replace
            }
        }
    }

    override fun clearAll() {
        if (showResult){
            calculatorText.text = "Ans = ${resultText.text}"
        }
        calculate.clear()
        showResult = false
        calculate.add("")
        resultText.text = "0"
    }

}