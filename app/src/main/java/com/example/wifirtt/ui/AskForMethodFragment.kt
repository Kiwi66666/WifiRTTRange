package com.example.wifirtt.ui

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.rtt.WifiRttManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.wifirtt.R
import com.example.wifirtt.databinding.FragmentAskForMethodBinding
import com.example.wifirtt.ui.data.Router
import com.example.wifirtt.ui.data.ScanRTTRouters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder


class AskForMethodFragment : Fragment() {
    private lateinit var binding: FragmentAskForMethodBinding
    private lateinit var scanResult: List<ScanResult>
    private lateinit var wifiRttManager: WifiRttManager
    private lateinit var wifiManager: WifiManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        wifiManager = activity?.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val filter = IntentFilter(WifiRttManager.ACTION_WIFI_RTT_STATE_CHANGED)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (wifiRttManager.isAvailable) {
                    Toast.makeText(context, "działa", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "nie działa", Toast.LENGTH_SHORT).show()
                }
            }
        }
        activity?.registerReceiver(receiver, filter)

        binding = FragmentAskForMethodBinding.inflate(inflater, container, false)

        binding.askForMethodList.setOnClickListener {
            scanForRTT()
            val navController = Navigation.findNavController(requireView())
            navController.navigate(R.id.action_askForMethodFragment_to_routerFragment)
        }
        binding.askForMethodFiles.setOnClickListener {
            val navController = Navigation.findNavController(requireView())
            navController.navigate((R.id.action_askForMethodFragment_to_routersFileFragment))

        }
        return binding.root
    }


    @SuppressLint("MissingPermission")
    fun scanForRTT() {

        wifiManager.startScan()

        scanResult = wifiManager.scanResults
        if (scanResult == null) {
            Toast.makeText(context, "pusta", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "jest coś", Toast.LENGTH_SHORT).show()
        }
        ScanRTTRouters.clearForRTT()
        for (item in scanResult) {
            if (item.is80211mcResponder) ScanRTTRouters.addForRTT(item)
        }
        Toast.makeText(context, "jest coś", Toast.LENGTH_SHORT).show()
    }
}