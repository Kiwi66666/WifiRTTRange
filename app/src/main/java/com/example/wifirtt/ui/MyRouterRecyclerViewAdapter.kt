package com.example.wifirtt.ui

import android.net.wifi.ScanResult
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.wifirtt.databinding.FragmentRouterBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyRouterRecyclerViewAdapter(
    private val values: List<ScanResult>
) : RecyclerView.Adapter<MyRouterRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentRouterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.SSID.text = item.SSID
        holder.BSSID.text = item.BSSID
        holder.distance.text = item.frequency.toString()
        holder.isRTT.text = item.is80211mcResponder.toString()
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentRouterBinding) : RecyclerView.ViewHolder(binding.root) {
        val SSID: TextView = binding.SSID
        val BSSID: TextView = binding.BSSID
        val distance: TextView = binding.distanceCm
        val isRTT : TextView = binding.isRTT
        val x = binding.editTextNumberDecimal2
        val y = binding.editTextNumberDecimal3

        override fun toString(): String {
            return super.toString() + " '" + BSSID.text + "'"
        }
    }

}