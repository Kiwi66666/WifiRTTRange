package com.example.wifirtt.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.wifirtt.R
import com.example.wifirtt.databinding.FragmentBoundingBoxBinding
import com.example.wifirtt.databinding.FragmentBoundingBoxFileBinding
import com.example.wifirtt.ui.data.BoundingBox
import com.example.wifirtt.ui.data.BoundingBox4Params
import com.example.wifirtt.ui.data.Router
import com.example.wifirtt.ui.data.ScanRTTRouters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder

class BoundingBoxFileFragment : Fragment() {
    private lateinit var binding: FragmentBoundingBoxFileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoundingBoxFileBinding.inflate(inflater, container, false)
        binding.boundingBoxFileButton.setOnClickListener {
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
                        val boxType = object : TypeToken<BoundingBox4Params>() {}.type
                        val boundingBoxJson = Gson().fromJson<BoundingBox4Params>(x, boxType)
                        if ((boundingBoxJson.down < boundingBoxJson.up) and (boundingBoxJson.left < boundingBoxJson.right)) {
                            var check = true
                            ScanRTTRouters.getListToRanging().forEach {
                                if ((it.x < boundingBoxJson.down) or (it.x > boundingBoxJson.up) or (it.y < boundingBoxJson.left) or (it.y > boundingBoxJson.right)) {
                                    check = false
                                }

                            }
                            if (check) {
                                BoundingBox.addParam(boundingBoxJson)
                                activity.let {
                                    val switchActivity = Intent(context, PositioningDrawActivity::class.java)
                                    startActivity(switchActivity)
                                }
                            }
                            else {
                                Toast.makeText(context, "Pozycja routera wykroczyła poza Bounding Box", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else {
                            Toast.makeText(context, "Podano złe parametry", Toast.LENGTH_SHORT).show()
                        }


                    }
                }


            }

        }

    }

}