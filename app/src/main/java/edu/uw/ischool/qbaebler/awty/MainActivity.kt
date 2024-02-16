package edu.uw.ischool.qbaebler.awty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    private lateinit var message: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var interval: EditText
    private lateinit var button: Button

    var isRunning : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("Main Activity", "did I run?")
        message = findViewById<EditText>(R.id.messageText)
        phoneNumber = findViewById<EditText>(R.id.phoneNumber)
        interval = findViewById<EditText>(R.id.intervalLength)
        button = findViewById<Button>(R.id.button)

        button.isEnabled = false
        button.isEnabled = isInputValid()

        button.setOnClickListener {
            if (isRunning == false) {
                button.text = "stop"
                startAwtyService()
                isRunning = true
            } else if (isRunning == true) {
                button.text = "start"
                isRunning = false
                stopAwtyService()
            }
        }

        message.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                button.isEnabled = isInputValid()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        phoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                button.isEnabled = isInputValid()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        interval.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                button.isEnabled = isInputValid()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun isInputValid(): Boolean {
       return message.text.isNotEmpty() &&
                phoneNumber.text.isNotEmpty() &&
                interval.text.isNotEmpty()
    }

    private fun startAwtyService() {
        val intent = Intent(this, AwtyService::class.java)
        intent.putExtra("message", message.text.toString())
        intent.putExtra("interval", interval.text.toString().toLong())
        startService(intent)
    }

    private fun stopAwtyService() {
        val intent = Intent(this, AwtyService::class.java)
        stopService(intent)
    }



}