package com.example.wifirtt.ui.data

import android.net.wifi.ScanResult

data class RouterToRanging(val scanResult: ScanResult, var x: Float, var y: Float, var xNorm: Float, var yNorm: Float )
