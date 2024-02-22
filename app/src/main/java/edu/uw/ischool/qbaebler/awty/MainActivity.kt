package edu.uw.ischool.qbaebler.awty

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import android.Manifest
import android.widget.Toast

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
        checkForSmsPermission()
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
        intent.putExtra("phoneNumber", phoneNumber.text.toString()) // Add this line
        startService(intent)
    }

    private fun stopAwtyService() {
        val intent = Intent(this, AwtyService::class.java)
        stopService(intent)
    }


    private fun checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MainActivity", "Permission not granted!")
            // Permission not yet granted. Use requestPermissions().
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS),
                1
            )
        } else {
            // Permission already granted. Enable the message button.
            button.isEnabled = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (permissions[0].equals(Manifest.permission.SEND_SMS, ignoreCase = true)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission was granted.
                    button.isEnabled = true
                } else {
                    // Permission denied.
                    Log.d("MainActivity", "Failed to obtain permission")
                    Toast.makeText(
                        this,
                        "Failed to obtain permission",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Disable the message button.
                    button.isEnabled = false
                }
            }
        }
    }
}