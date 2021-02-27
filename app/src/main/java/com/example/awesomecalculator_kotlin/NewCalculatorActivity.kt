package com.example.awesomecalculator_kotlin

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.awesomecalculator_kotlin.adapters.CalculatorAdapter
import com.example.awesomecalculator_kotlin.callbacks.btnCallbacks
import java.lang.StringBuilder


class NewCalculatorActivity : AppCompatActivity(), btnCallbacks {
    val TAG:String = "CalculatorMain"
    // List of all the keys that we want
    val Verticalkeys:List<String> = listOf(
        "AC",
        "CE",
        "7",
        "8",
        "9",
        "/",
        "4",
        "5",
        "6",
        "x",
        "1",
        "2",
        "3",
        "-",
        "0",
        ".",
        "=",
        "+",
    )
    // INIT VALUE
    private lateinit var calculatorText: TextView
    private lateinit var resultText: TextView
    private var calculation:StringBuilder = StringBuilder()
    private var limitText:Int = 25
    private var showResult:Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init(){
        // Start the initialization of the activity
        val recyclerView:RecyclerView = findViewById(R.id.keys_grid)
        calculatorText = findViewById(R.id.calculationText)
        resultText = findViewById(R.id.resultText)
        resultText.text = "0"
        val glm = GridLayoutManager(this, 4)
        glm.setSpanSizeLookup(object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (Verticalkeys.get(position) == "AC" || Verticalkeys.get(position) == "CE") 2 else 1
            }
        })
        recyclerView.layoutManager = glm
        val recyclerViewAdapter = CalculatorAdapter(this, Verticalkeys)
        recyclerView.adapter = recyclerViewAdapter
    }

    override fun addCommands(cmd: String) {
        // Checking limit
        var numberChars = calculation.replace("\\s".toRegex(), "")
        if (numberChars.length < limitText) {
            when (cmd) {
                "+", "x", "/", "-" -> {
                    if (calculation.isEmpty()) {
                        if (cmd == "-") {
                            calculation.append(cmd)
                        } else {
                            calculation.append("0 $cmd ")
                        }
                    } else
                        if (calculation.takeLast(2).contains(Regex("""[x\/+-]\s"""))) {
                            calculation.replace(
                                calculation.length - 3,
                                calculation.length,
                                " $cmd "
                            )
                        } else {
                            calculation.append(" $cmd ")
                        }
                }
                else -> {
                    calculation.append(cmd)
                }
            }
            resultText.text = calculation
        }else{
            Toast.makeText(this, R.string.limit, Toast.LENGTH_SHORT).show()
        }
    }

    fun checkInteger(value:String):String{
        if (value.endsWith(".0")){
           return value.substring(0, value.length - 2);
        }
            return value
    }

    fun doCalculations(s:String):String{
        val t = s.split(" ")
        val firstval = t[0]
        if (firstval.isNotEmpty()){
            when(t[1]){
                "x"->{
                    return (firstval.toDouble() * t[2].toDouble()).toString()
                }
                "/"->{
                    return (firstval.toDouble() / t[2].toDouble()).toString()
                }
                "-" -> {
                    return (firstval.toDouble() - t[2].toDouble()).toString()
                }
                "+" -> {
                    return (firstval.toDouble() + t[2].toDouble()).toString()
                }
            }}

        return s
    }

    // Create check if double function
    override fun calculate() {
        //Check if there is a sign at the end of the function
        val checkSign = Regex("(\\s[+-\\/x]\\s)\$")
        var current = calculation.replace(checkSign, "")

        // Do calculation
        // Multiply + Divide
        val pattern1 = Regex("(\\d*\\.?\\d*\\s[x\\/]\\s\\d*\\.?\\d*)")
        while(pattern1.containsMatchIn(current)){
            current = current.replace(pattern1) {m ->
                val split = doCalculations(m.value);
                split
            }
        }
        // Add + substract
        val pattern2 = Regex("(\\d*\\.?\\d*\\s[+-]\\s\\d*\\.?\\d*)")
        while(pattern2.containsMatchIn(current)){
            current = current.replace(pattern2) {m ->
                val split = doCalculations(m.value);
                split
            }
        }
        // Check type
        current = checkInteger(current)

        if(checkSign.containsMatchIn(calculation)){
            // Keep the result
            calculatorText.text = "Ans = $current"
        }else{
            calculatorText.text = calculation.append(" = ")
            resultText.text = current
            calculation = StringBuilder(current)
        }

    }


    override fun deleteCommands() {
        if(calculation.endsWith(" ")){
            calculation.deleteRange(calculation.length - 3, calculation.length)
            resultText.text = calculation
        }else if (calculation.isNotEmpty()) {
            calculation.deleteCharAt(calculation.length - 1)
            resultText.text = calculation
        }else{
            resultText.text = "0"
        }
    }

    override fun clearAll() {
        if (showResult){
            calculatorText.text = "Ans = $calculation"
            calculation.clear()
            resultText.text = "0"
            showResult = false
        }else{
            calculation.clear()
            resultText.text = "0"
        }

    }
}