package com.example.awesomecalculator_kotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.awesomecalculator_kotlin.R
import com.example.awesomecalculator_kotlin.callbacks.btnCallbacks


// Calculate the real size and resize the view holders
// Add the different values to the answer tab for calculation
class CalculatorAdapter(
    private val callbackInterface: btnCallbacks,
    private val listOfKeys: List<String>
):RecyclerView.Adapter<CalculatorAdapter.ViewHolder>() {

    private val TAG:String = "Adapter"

    // View from the layout
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val button: Button

        init {
            // Define click listener for the ViewHolder's View.
            button = view.findViewById(R.id.calculator_btn)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var height:Int = parent.measuredHeight / 5
        val view:View = if(listOfKeys[viewType].contains(Regex("""[0-9.]"""))){
            LayoutInflater.from(parent.context).inflate(R.layout.number_button, parent, false)
        }else if (listOfKeys[viewType].equals("=")) {
            LayoutInflater.from(parent.context).inflate(R.layout.equals_button, parent, false)
        }else{
            LayoutInflater.from(parent.context).inflate(R.layout.sign_button, parent, false)
        }
        // Only first values are larger
        var width:Int = if(listOfKeys[viewType] == "AC" || listOfKeys[viewType] == "CE"){
            parent.measuredWidth/ 2
        }else {
            parent.measuredWidth/4
        }
        view.layoutParams = ViewGroup.LayoutParams(width, height)
        return ViewHolder(view)
    }

    // Changing view types
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.button.text = listOfKeys[position]
        holder.button.setOnClickListener { it: View? ->
            // Do some work here
            val b = it as Button
            when (val buttonText = b.text.toString()) {
                "AC" -> {
                    callbackInterface.clearAll()
                }
                "CE" -> {
                    callbackInterface.deleteCommands()
                }
                "=" -> {
                    callbackInterface.calculate()
                }
                else -> {
                    callbackInterface.addCommands(listOfKeys[position])
                }
            }
        }
    }

    override fun getItemCount(): Int = listOfKeys.size

}