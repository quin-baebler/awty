package edu.uw.ischool.qbaebler.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast

class AwtyService : Service() {

    private var message: String? = null
    private var interval: Long = 0
    private var phoneNumber: String? = null
    private lateinit var pendingIntent: PendingIntent
    private val toastReceiver = ToastReceiver()

    inner class ToastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val phoneNumber = intent.getStringExtra("phoneNumber") ?: return
            val message = intent.getStringExtra("message") ?: return
            sendSms(phoneNumber, message)
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(toastReceiver, IntentFilter("com.example.alarm_trigger"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        message = intent?.getStringExtra("message")
        interval = intent?.getLongExtra("interval", 0) ?: 0
        val phoneNumber = intent?.getStringExtra("phoneNumber") ?: return START_NOT_STICKY
        Toast.makeText(this, "$phoneNumber: $message", Toast.LENGTH_SHORT).show()
        scheduleAlarm()
        message?.let { sendSms(phoneNumber, it) }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(toastReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun scheduleAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AwtyService::class.java).apply {
            putExtra("message", message)
            putExtra("phoneNumber", phoneNumber)
        }
        pendingIntent = PendingIntent.getBroadcast(
            this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val triggerTime = System.currentTimeMillis() + interval * 60000
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            interval * 60000,
            pendingIntent
        )
    }


    private fun sendSms(phoneNumber: String, message: String) {
        try {
            val smsManager =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    getSystemService(SmsManager::class.java)
                else
                    SmsManager.getDefault()
            Log.i("sendingMessage", "Yuhyuh")
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        } catch (e: Exception) {
            // Handle sending error
            Log.e("AwtyService", "Error sending SMS: $e")
        }
    }
}