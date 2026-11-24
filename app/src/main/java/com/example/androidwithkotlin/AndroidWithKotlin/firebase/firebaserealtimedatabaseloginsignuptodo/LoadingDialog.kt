package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebaserealtimedatabaseloginsignuptodo

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class LoadingDialog(context: Context) {
    private val dialog: AlertDialog

    init {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(50, 40, 50, 40)
            setBackgroundColor(Color.WHITE)
        }

        val progressBar = ProgressBar(context).apply {
            isIndeterminate = true
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = 40
            }
        }

        val textView = TextView(context).apply {
            text = "Loading..."
            setTextColor(Color.BLACK)
            textSize = 16f
        }

        layout.addView(progressBar)
        layout.addView(textView)

        dialog = AlertDialog.Builder(context)
            .setView(layout)
            .setCancelable(false)
            .create()
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}
