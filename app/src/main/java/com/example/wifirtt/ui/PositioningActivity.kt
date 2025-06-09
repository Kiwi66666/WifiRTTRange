package com.example.wifirtt.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.wifirtt.databinding.ActivityPositioningBinding
import com.example.wifirtt.ui.data.ScanRTTRouters
import java.util.Timer
import java.util.concurrent.Executor
import java.util.logging.Handler
import kotlin.concurrent.schedule

class PositioningActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPositioningBinding
    private lateinit var mgr : WifiRttManager
    private lateinit var rttRanging : RTTRangingResultCallback

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mgr = applicationContext.getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as WifiRttManager
        rttRanging = RTTRangingResultCallback()
        binding = ActivityPositioningBinding.inflate(layoutInflater)
        rttRanging = RTTRangingResultCallback()
        setContentView(binding.root)

        val filter = IntentFilter(WifiRttManager.ACTION_WIFI_RTT_STATE_CHANGED)

        startRangingRequest()




    }
    @SuppressLint("MissingPermission")
    private fun startRangingRequest() {
        val rangingRequest = RangingRequest.Builder().addAccessPoints(ScanRTTRouters.getListForRTT()).build()
        mgr.startRanging(rangingRequest, mainExecutor, rttRanging)
    }
    private inner class RTTRangingResultCallback : RangingResultCallback() {

        fun queueRequest() {
            val handler = android.os.Handler()
            handler.postDelayed(
                Runnable { run {
                    startRangingRequest()

                } }, 500
            )

        }

        override fun onRangingResults(results: List<RangingResult>) {
            binding.textViewX.text = ScanRTTRouters.getListToScan()[0].x.toString()
            binding.textViewY.text = ScanRTTRouters.getListToScan()[0].y.toString()
            binding.textViewbssid.text = ScanRTTRouters.getListToScan()[0].scanResult.BSSID
            binding.textView4.text = results[0].distanceMm.toString()
            binding.textView5.text = results[0].distanceStdDevMm.toString()
            queueRequest()
        }

        override fun onRangingFailure(p0: Int) {
            Log.d("s", "nie działa")
            queueRequest()

        }
    }
}