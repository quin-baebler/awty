package edu.uw.ischool.qbaebler.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast

 class AwtyService: Service() {

     private var message: String? = null
     private var interval: Long = 0
     private lateinit var pendingIntent: PendingIntent
     private val toastReceiver = ToastReceiver()

     class ToastReceiver : BroadcastReceiver() {
         override fun onReceive(context: Context, intent: Intent) {
             Toast.makeText(context, "(425) 555-1212: ${intent.getStringExtra("message")}", Toast.LENGTH_SHORT).show()
         }
     }


    override fun onCreate() {
        super.onCreate()

        registerReceiver(toastReceiver, IntentFilter("com.example.alarm_trigger"))
        val alarmIntent = Intent(this, AwtyService::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            this, 0, Intent("com.example.alarm_trigger").putExtra("message", message),
            PendingIntent.FLAG_UPDATE_CURRENT)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        message = intent!!.getStringExtra("message")
        interval = intent.getLongExtra("interval", 0)
        Toast.makeText(this, "(425) 555-1212: $message", Toast.LENGTH_SHORT).show()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + interval * 60000


        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            interval * 60000,
            pendingIntent
        )

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(toastReceiver)
    }

     override fun onBind(intent: Intent?): IBinder? {
         TODO("Not yet implemented")
     }
 }