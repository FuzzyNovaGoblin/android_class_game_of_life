package com.fuzytech.game_of_life

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.widget.EditText
import android.widget.TextView

object DialogUtil {

    fun showDialog(context: Context, prompt: String, callback: (String) -> Unit) {
        val builder = AlertDialog.Builder(context)
        val input = EditText(context)
        input.hint = prompt
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("Ok") { _, _ -> callback(input.text.toString()) }
        builder.setNegativeButton("Cancel") {dialog, _ -> dialog.cancel()}
        builder.show()
    }

    fun showAlert(context: Context, info: String) {
        val builder = AlertDialog.Builder(context)
        val text = TextView(context)
        text.text = info
        builder.setView(text)
        builder.setPositiveButton("Ok") {dialog, _ -> dialog.cancel()}
        builder.show()
    }

}