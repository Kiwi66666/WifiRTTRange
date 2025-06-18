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
import android.net.wifi.rtt.RangingResult.STATUS_SUCCESS
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.updateLayoutParams
import com.example.wifirtt.databinding.ActivityPositioningDrawBinding
import com.example.wifirtt.ui.data.BoundingBox
import com.example.wifirtt.ui.data.Point
import com.example.wifirtt.ui.data.RouterToRanging
import com.example.wifirtt.ui.data.ScanRTTRouters
import java.util.Timer
import java.util.concurrent.Executor
import java.util.logging.Handler
import kotlin.concurrent.schedule
import kotlin.random.Random

class PositioningDrawActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPositioningDrawBinding
    private lateinit var mgr : WifiRttManager
    private lateinit var rttRanging : RTTRangingResultCallback
    private var checkScreen = true
    private var numberOfScans = 0
    private var numbersOfScansToDraw = arrayOf(0,0,0)
    private var sumsOfScans = arrayOf(0F,0F,0F)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mgr = applicationContext.getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as WifiRttManager
        rttRanging = RTTRangingResultCallback()


        binding = ActivityPositioningDrawBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val filter = IntentFilter(WifiRttManager.ACTION_WIFI_RTT_STATE_CHANGED)
        binding.router1.text = ScanRTTRouters.getListToRanging()[0].scanResult.BSSID
        binding.router2.text = ScanRTTRouters.getListToRanging()[1].scanResult.BSSID
        binding.router3.text = ScanRTTRouters.getListToRanging()[2].scanResult.BSSID
        startRangingRequest(0, 0, 0)



    }
    @SuppressLint("MissingPermission")
    private fun startRangingRequest(d1: Int, d2: Int, d3: Int) {
        val rangingRequest = RangingRequest.Builder().addAccessPoints(ScanRTTRouters.getListForRTT()).build()
        mgr.startRanging(rangingRequest, mainExecutor, rttRanging)
        numberOfScans++
        binding.numberOfScans.text=numberOfScans.toString()
        if(d1<200000){
            sumsOfScans[0] += (d1.toFloat()+3300)/1.5F
            numbersOfScansToDraw[0]++
        }
        if(d2<200000){
            sumsOfScans[1] += (d2.toFloat()-1000)/1.5F
            numbersOfScansToDraw[1]++
        }
        if(d3<200000){
            sumsOfScans[2] += (d3.toFloat()-1000)/1.5F
            numbersOfScansToDraw[2]++
        }
        binding.router1Scans.text=numbersOfScansToDraw[0].toString()
        binding.router2Scans.text=numbersOfScansToDraw[1].toString()
        binding.router3Scans.text=numbersOfScansToDraw[2].toString()
        if (checkScreen) { //przy pierwszym range trzeba zainicjować
            var sizeOfView = calculateArea(binding.position.height, binding.position.width)
            if (sizeOfView[0] != 0) {
                binding.position.updateLayoutParams {
                    width = sizeOfView[0]
                    height = sizeOfView[1]
                }
                checkScreen = false
            }
            addRoutersToDraw(ScanRTTRouters.getListToRanging(), binding.position.height.toFloat(), binding.position.width.toFloat())
            val point = Point(0F, 0F,10F, true)


            binding.position.setPoint(point)
        }
        if(numberOfScans==10) {

            addRoutersToDraw(
                ScanRTTRouters.getListToRanging(),
                binding.position.height.toFloat(),
                binding.position.width.toFloat()
            )
            val xyMeters = trilateration(sumsOfScans[0]/(numbersOfScansToDraw[0]*1000), sumsOfScans[1]/(numbersOfScansToDraw[1]*1000), sumsOfScans[2]/(numbersOfScansToDraw[2]*1000))
            val xyCart = convertMetersToCart(
                binding.position.width.toFloat(),
                binding.position.height.toFloat(),
                xyMeters[0],
                xyMeters[1]
            )
            val point = Point(xyCart[0], xyCart[1], 10F, false)

            numberOfScans = 0
            numbersOfScansToDraw = arrayOf(0,0,0)
            sumsOfScans = arrayOf(0F,0F,0F)
            binding.position.setPoint(point)

        }
    }
    private inner class RTTRangingResultCallback : RangingResultCallback() {

        fun queueRequest(d1: Int, d2: Int, d3: Int) {
            val handler = android.os.Handler()
            handler.postDelayed(
                Runnable { run {
                    startRangingRequest(d1, d2, d3)

                } }, 200
            )

        }

        override fun onRangingResults(results: List<RangingResult>) {
            if(results[0].status == STATUS_SUCCESS && results[1].status == STATUS_SUCCESS && results[2].status == STATUS_SUCCESS) {
                queueRequest(
                    results[0].distanceMm,
                    results[1].distanceMm,
                    results[2].distanceMm
                )
            }
        }

        override fun onRangingFailure(p0: Int) {
            Log.d("s", "nie działa")
            startRangingRequest(0, 0, 0)
            Toast.makeText(this@PositioningDrawActivity, "jest coś", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addRoutersToDraw(r: MutableList<RouterToRanging>, x: Float, y:Float) {
        val routers: MutableList<Point> = ArrayList()
        r.forEach {
            val xy = convertNormMetersToCart(x, y, it.xNorm, it.yNorm)
            val router = Point(xy[0], xy[1], 10F, true)
            routers.add(router)
        }
        binding.position.addRouter(routers)
    }
    private fun trilateration(d1: Float, d2: Float, d3: Float): Array<Float> { //argumenty w metrach
        val r1 = ScanRTTRouters.getListToRanging()[0]
        val r2 = ScanRTTRouters.getListToRanging()[1]
        val r3 = ScanRTTRouters.getListToRanging()[2]
        val a = 2*r2.x - 2*r1.x
        val b = 2*r2.y - 2*r1.y
        val c = d1*d1 - d2*d2 - r1.x*r1.x + r2.x*r2.x - r1.y*r1.y + r2.y*r2.y
        val d = 2*r3.x - 2*r2.x
        val e = 2*r3.y - 2*r2.y
        val f = d2*d2 - d3*d3 - r2.x*r2.x + r3.x*r3.x - r2.y*r2.y + r3.y*r3.y
        val x = (c*e - f*b)/(e*a - b*d)
        val y = (c*d - a*f)/(b*d - a*e)
        return arrayOf(x,y)
    }
    private fun convertMetersToCart(xCart: Float, yCart: Float, x: Float, y: Float):Array<Float> {
        var help = x - BoundingBox.original.left
        val xNorm = help
        help = BoundingBox.original.up - y
        val yNorm = help
        val rx = xNorm/BoundingBox.x
        val ry = yNorm/BoundingBox.y
        if (rx > 1.0F || ry > 1.0F) {
            return arrayOf(0F,0F)
        }
        return arrayOf(rx*xCart, ry*yCart)
    }
    private fun convertNormMetersToCart(xCart: Float, yCart: Float, x: Float, y: Float):Array<Float> {
        val rx = x/BoundingBox.x
        val ry = y/BoundingBox.y
        if (rx > 1.0F || ry > 1.0F) {
            return arrayOf(0F,0F)
        }
        return arrayOf(rx*xCart, ry*yCart)
    }
    private fun calculateArea(x: Int, y: Int): Array<Int> {
        if (BoundingBox.x > BoundingBox.y) {
            val ratio = BoundingBox.y/BoundingBox.x
            return arrayOf((y*ratio).toInt(), x)
        }
        else if (BoundingBox.y > BoundingBox.x) {
            val ratio = BoundingBox.x/BoundingBox.y
            return arrayOf(y, (x*ratio).toInt())
        }
        return arrayOf(x, y)
    }
}