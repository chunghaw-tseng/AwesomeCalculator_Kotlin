package com.example.awesomecalculator_kotlin.callbacks

interface btnCallbacks {
    fun addCommands(cmd: String)
    fun deleteCommands()
    fun clearAll()
    fun calculate()
}