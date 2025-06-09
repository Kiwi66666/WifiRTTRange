
package com.example.wifirtt.ui

import android.annotation.SuppressLint
import android.app.Activity
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
import com.example.wifirtt.databinding.FragmentRoutersFileBinding
import com.example.wifirtt.ui.data.Router
import com.example.wifirtt.ui.data.ScanRTTRouters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder


class RoutersFileFragment : Fragment() {

    private lateinit var binding: FragmentRoutersFileBinding
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
        binding = FragmentRoutersFileBinding.inflate(inflater, container, false)
        binding.routersFileButton.setOnClickListener {
            scanForRTT()
            val intent = Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }
        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val contentResolver = activity?.contentResolver
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                val stringBuilder = StringBuilder()
                contentResolver?.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var line: String? = reader.readLine()
                        while (line != null) {
                            stringBuilder.append(line)
                            line = reader.readLine()
                        }
                        var x: String = ""
                        for (i in stringBuilder) {
                            x += i.toString()
                        }
                        val listType = object : TypeToken<ArrayList<Router>>() {}.type
                        val listOfRoutersJson = Gson().fromJson<ArrayList<Router>>(x, listType)
                        val i = ScanRTTRouters.checkRouters(listOfRoutersJson)
                        when (i.code) {
                            0 -> Toast.makeText(context, "Za mała liczba routerów na liście", Toast.LENGTH_SHORT).show()
                            1 -> {val navController = Navigation.findNavController(requireView())
                                navController.navigate(R.id.action_routersFileFragment_to_boundingBoxFileFragment)}
                            2 -> Toast.makeText(context, "Nie znalezionno routerów "+i.list+" więc nie ma wsystarczającej liczby", Toast.LENGTH_SHORT).show()
                            3 -> Toast.makeText(context, "Nie znalezionno routerów "+i.list+" ale są 3 routery", Toast.LENGTH_SHORT).show() //zmienić toasty na dialogi
                        }
                    }
                }


            }

        }

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